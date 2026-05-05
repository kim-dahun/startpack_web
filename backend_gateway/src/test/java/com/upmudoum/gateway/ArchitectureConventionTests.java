package com.upmudoum.gateway;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArchitectureConventionTests {

    @Test
    void gatewayMainSourcesDoNotUseJavaRecords() throws IOException {
        Path sourceRoot = Path.of("src/main/java");
        boolean hasRecord = Files.walk(sourceRoot)
                .filter(path -> path.toString().endsWith(".java"))
                .map(this::readSource)
                .anyMatch(source -> source.contains(" record "));

        assertThat(hasRecord).isFalse();
    }

    @Test
    void gatewayJpaSchemaDefaultsToGatewayServiceOnly() {
        String applicationProperties = readSource(Path.of("src/main/resources/application.properties"));

        assertThat(applicationProperties)
                .contains("spring.datasource.url=${DEFAULT_DB_URL:jdbc:postgresql://localhost:5432/postgres?currentSchema=gateway_service}")
                .contains("spring.datasource.username=${DEFAULT_DB_ID:}")
                .contains("spring.datasource.password=${DEFAULT_DB_PASSWORD:}")
                .contains("spring.datasource.hikari.schema=${GATEWAY_DB_SCHEMA:gateway_service}")
                .contains("spring.jpa.properties.hibernate.default_schema=${GATEWAY_DB_SCHEMA:gateway_service}")
                .contains("spring.jpa.hibernate.ddl-auto=none");
    }

    @Test
    void gatewayDefaultCorsAllowsFrontendDevOriginsWithCredentialsAndOptions() {
        String applicationProperties = readSource(Path.of("src/main/resources/application.properties"));

        assertThat(applicationProperties)
                .contains("gateway.cors.allowed-origins=${GATEWAY_CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:5173,http://localhost:5176,http://localhost:5177,http://localhost:5178}")
                .contains("gateway.cors.allowed-methods=${GATEWAY_CORS_ALLOWED_METHODS:GET,POST,PUT,PATCH,DELETE,OPTIONS}")
                .contains("gateway.cors.allow-credentials=${GATEWAY_CORS_ALLOW_CREDENTIALS:true}")
                .contains("X-Request-Id")
                .contains("X-Open-Api-Token")
                .contains("X-Requested-With");
    }

    @Test
    void gatewayUsesUnifiedInternalGatewayHeaders() {
        String applicationProperties = readSource(Path.of("src/main/resources/application.properties"));

        assertThat(applicationProperties)
                .contains("gateway.internal.gateway-id=${GATEWAY_INTERNAL_GATEWAY_ID:backend_gateway}")
                .contains("gateway.internal.gateway-secret=${GATEWAY_INTERNAL_GATEWAY_SECRET:local-dev-gateway-secret}")
                .contains("gateway.routes.internal-paths[0]=/api/auth/login")
                .doesNotContain("gateway.internal.user-header-name")
                .doesNotContain("gateway.internal.shared-header-name")
                .doesNotContain("gateway.internal.trade-header-name");
    }

    @Test
    void gatewayDeclaresTradeWebSocketBridgeRouteSeparatelyFromHttpApiRoutes() {
        String applicationProperties = readSource(Path.of("src/main/resources/application.properties"));
        String webSocketConfiguration = readSource(Path.of(
                "src/main/java/com/upmudoum/gateway/config/GatewayWebSocketConfiguration.java"));
        String bridgeFactory = readSource(Path.of(
                "src/main/java/com/upmudoum/gateway/gateway/websocket/TradeWebSocketBridgeRequestFactory.java"));

        assertThat(applicationProperties)
                .contains("gateway.websocket.trade-realtime-path=${GATEWAY_WS_TRADE_REALTIME_PATH:/ws/trade/realtime}")
                .contains("gateway.websocket.trade-realtime-backend-path=${GATEWAY_WS_TRADE_REALTIME_BACKEND_PATH:/ws/trade/realtime}")
                .contains("gateway.websocket.forward-browser-cookies=${GATEWAY_WS_FORWARD_BROWSER_COOKIES:false}");
        assertThat(webSocketConfiguration)
                .contains("registerWebSocketHandlers")
                .contains("properties.getWebsocket().getTradeRealtimePath()");
        assertThat(bridgeFactory)
                .contains("GatewayHeaders.INTERNAL_GATEWAY_ID")
                .contains("GatewayHeaders.INTERNAL_GATEWAY_SECRET")
                .contains("isForwardBrowserCookies()");
    }

    @Test
    void gatewayConfiguresAuthRefreshPathForCookieBasedRequests() {
        String applicationProperties = readSource(Path.of("src/main/resources/application.properties"));

        assertThat(applicationProperties)
                .contains("gateway.auth.refresh-path=${GATEWAY_AUTH_REFRESH_PATH:/api/auth/tokens/refresh}")
                .contains("gateway.auth.refresh-token-cookie-name=${GATEWAY_AUTH_REFRESH_TOKEN_COOKIE_NAME:REFRESH_TOKEN}")
                .contains("gateway.routes.public-paths[4]=/api/auth/tokens/refresh")
                .contains("gateway.routes.public-paths[6]=/api/v1/auth/tokens/refresh");
    }

    @Test
    void gatewayLoginContractSeparatesPublicUserLoginFromInternalTokenIssue() {
        String applicationProperties = readSource(Path.of("src/main/resources/application.properties"));
        String routeConfiguration = readSource(Path.of(
                "src/main/java/com/upmudoum/gateway/gateway/route/GatewayRouteConfiguration.java"));

        assertThat(applicationProperties)
                .contains("gateway.routes.public-paths[2]=/api/users/login")
                .contains("gateway.routes.public-paths[3]=/api/users/signup")
                .contains("gateway.routes.internal-paths[0]=/api/auth/login")
                .doesNotContain("gateway.routes.public-paths[2]=/api/auth/login")
                .doesNotContain("gateway.routes.public-paths[6]=/api/v1/auth/login");
        assertThat(routeConfiguration)
                .contains(".route(path(\"/api/auth/**\"), http(services.getAuthUrl()))")
                .doesNotContain(".route(path(\"/api/internal/auth/**\"), http(services.getAuthUrl()))");
    }

    @Test
    void gatewayDoesNotDeclareJpaEntitiesOrRepositories() throws IOException {
        Path sourceRoot = Path.of("src/main/java");
        List<String> sources = Files.walk(sourceRoot)
                .filter(path -> path.toString().endsWith(".java"))
                .map(this::readSource)
                .toList();

        assertThat(sources).allSatisfy(source -> assertThat(source)
                .doesNotContain("@Entity")
                .doesNotContain("jakarta.persistence.Entity")
                .doesNotContain("JpaRepository")
                .doesNotContain("CrudRepository")
                .doesNotContain("org.springframework.data.repository"));
    }

    @Test
    void gatewayMainSourcesDoNotUseLombokData() throws IOException {
        Path sourceRoot = Path.of("src/main/java");
        boolean usesLombokData = Files.walk(sourceRoot)
                .filter(path -> path.toString().endsWith(".java"))
                .map(this::readSource)
                .anyMatch(source -> source.contains("@Data") || source.contains("lombok.Data"));

        assertThat(usesLombokData).isFalse();
    }

    private String readSource(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException ex) {
            throw new IllegalStateException("failed to read " + path, ex);
        }
    }
}
