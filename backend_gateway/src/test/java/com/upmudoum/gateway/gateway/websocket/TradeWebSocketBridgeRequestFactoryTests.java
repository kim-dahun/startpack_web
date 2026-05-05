package com.upmudoum.gateway.gateway.websocket;

import com.upmudoum.gateway.common.GatewayHeaders;
import com.upmudoum.gateway.config.GatewayProperties;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.WebSocketHttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;

class TradeWebSocketBridgeRequestFactoryTests {

    @Test
    void buildsBackendWebSocketUriFromTradeHttpRouteAndKeepsQueryString() {
        GatewayProperties properties = testProperties();
        TradeWebSocketBridgeRequestFactory factory = new TradeWebSocketBridgeRequestFactory(properties);

        URI backendUri = factory.backendUri(URI.create("ws://localhost:9091/ws/trade/realtime?symbol=005930"));

        assertThat(backendUri).isEqualTo(URI.create("ws://localhost:9096/ws/trade/realtime?symbol=005930"));
    }

    @Test
    void injectsInternalGatewayHeadersAndAuthenticatedContextButDoesNotForwardCookiesByDefault() {
        GatewayProperties properties = testProperties();
        TradeWebSocketBridgeRequestFactory factory = new TradeWebSocketBridgeRequestFactory(properties);
        HttpHeaders frontendHeaders = new HttpHeaders();
        frontendHeaders.add(GatewayHeaders.REQUEST_ID, "request-1");
        frontendHeaders.add(GatewayHeaders.USER_ID, "user-1");
        frontendHeaders.add(GatewayHeaders.COMPANY_CODE, "COMPANY");
        frontendHeaders.add(GatewayHeaders.ROLES, "ROLE_USER");
        frontendHeaders.add(GatewayHeaders.TOKEN_TYPE, "ACCESS");
        frontendHeaders.add(HttpHeaders.COOKIE, "ACCESS_TOKEN=access; REFRESH_TOKEN=refresh");

        WebSocketHttpHeaders backendHeaders = factory.backendHeaders(frontendHeaders);

        assertThat(backendHeaders.getFirst(GatewayHeaders.INTERNAL_GATEWAY_ID)).isEqualTo("backend_gateway");
        assertThat(backendHeaders.getFirst(GatewayHeaders.INTERNAL_GATEWAY_SECRET)).isEqualTo("local-dev-gateway-secret");
        assertThat(backendHeaders.getFirst(GatewayHeaders.REQUEST_ID)).isEqualTo("request-1");
        assertThat(backendHeaders.getFirst(GatewayHeaders.USER_ID)).isEqualTo("user-1");
        assertThat(backendHeaders.getFirst(GatewayHeaders.COMPANY_CODE)).isEqualTo("COMPANY");
        assertThat(backendHeaders.getFirst(GatewayHeaders.ROLES)).isEqualTo("ROLE_USER");
        assertThat(backendHeaders.getFirst(GatewayHeaders.TOKEN_TYPE)).isEqualTo("ACCESS");
        assertThat(backendHeaders.getFirst(HttpHeaders.COOKIE)).isNull();
    }

    @Test
    void forwardsBrowserCookiesOnlyWhenExplicitlyEnabled() {
        GatewayProperties properties = testProperties();
        properties.getWebsocket().setForwardBrowserCookies(true);
        TradeWebSocketBridgeRequestFactory factory = new TradeWebSocketBridgeRequestFactory(properties);
        HttpHeaders frontendHeaders = new HttpHeaders();
        frontendHeaders.add(HttpHeaders.COOKIE, "ACCESS_TOKEN=access; REFRESH_TOKEN=refresh");

        WebSocketHttpHeaders backendHeaders = factory.backendHeaders(frontendHeaders);

        assertThat(backendHeaders.getFirst(HttpHeaders.COOKIE))
                .isEqualTo("ACCESS_TOKEN=access; REFRESH_TOKEN=refresh");
    }

    private GatewayProperties testProperties() {
        GatewayProperties properties = new GatewayProperties();
        properties.getRoutes().getServices().setTradeUrl(URI.create("http://localhost:9096"));
        properties.getInternal().setGatewayId("backend_gateway");
        properties.getInternal().setGatewaySecret("local-dev-gateway-secret");
        properties.getWebsocket().setTradeRealtimePath("/ws/trade/realtime");
        properties.getWebsocket().setTradeRealtimeBackendPath("/ws/trade/realtime");
        return properties;
    }
}
