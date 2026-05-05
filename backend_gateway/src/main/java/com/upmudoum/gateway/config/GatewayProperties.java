package com.upmudoum.gateway.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "gateway")
@Getter
public class GatewayProperties {

    @Valid
    private final Routes routes = new Routes();

    @Valid
    private final Auth auth = new Auth();

    @Valid
    private final Cors cors = new Cors();

    @Valid
    private final Logging logging = new Logging();

    @Valid
    private final Authorization authorization = new Authorization();

    @Valid
    private final Internal internal = new Internal();

    @Valid
    private final Websocket websocket = new Websocket();

    @Getter
    public static class Routes {

        @Valid
        private final Services services = new Services();

        private final List<String> publicPaths = new ArrayList<>();

        private final List<String> internalPaths = new ArrayList<>();

        private final List<String> openApiPaths = new ArrayList<>();

    }

    @Getter
    @Setter
    public static class Services {

        @NotNull(message = "gateway.routes.services.auth-url is required")
        private URI authUrl;

        @NotNull(message = "gateway.routes.services.user-url is required")
        private URI userUrl;

        @NotNull(message = "gateway.routes.services.erp-url is required")
        private URI erpUrl;

        @NotNull(message = "gateway.routes.services.groupware-url is required")
        private URI groupwareUrl;

        @NotNull(message = "gateway.routes.services.trade-url is required")
        private URI tradeUrl;

    }

    @Getter
    @Setter
    public static class Auth {

        @NotNull(message = "gateway.auth.verify-path is required")
        private String verifyPath;

        @NotNull(message = "gateway.auth.refresh-path is required")
        private String refreshPath = "/api/auth/tokens/refresh";

        @NotNull(message = "gateway.auth.timeout is required")
        private Duration timeout;

        @NotNull(message = "gateway.auth.access-token-cookie-name is required")
        private String accessTokenCookieName = "ACCESS_TOKEN";

        @NotNull(message = "gateway.auth.refresh-token-cookie-name is required")
        private String refreshTokenCookieName = "REFRESH_TOKEN";

        @Valid
        private final Cache cache = new Cache();

        @Valid
        private final CircuitBreaker circuitBreaker = new CircuitBreaker();

    }

    @Getter
    @Setter
    public static class CircuitBreaker {

        private String name = "authVerification";

        private float failureRateThreshold = 50.0f;

        private int slidingWindowSize = 20;

        private int permittedNumberOfCallsInHalfOpenState = 5;

        @NotNull(message = "gateway.auth.circuit-breaker.wait-duration-in-open-state is required")
        private Duration waitDurationInOpenState = Duration.ofSeconds(10);

    }

    @Getter
    @Setter
    public static class Cache {

        private boolean enabled = true;

        @NotNull(message = "gateway.auth.cache.ttl is required")
        private Duration ttl = Duration.ofSeconds(5);

        private int maxSize = 1000;

    }

    @Getter
    @Setter
    public static class Cors {

        private final List<String> allowedOrigins = new ArrayList<>();

        private final List<String> allowedMethods = new ArrayList<>();

        private final List<String> allowedHeaders = new ArrayList<>();

        private boolean allowCredentials;

        @NotNull(message = "gateway.cors.max-age is required")
        private Duration maxAge = Duration.ofHours(1);

    }

    @Getter
    @Setter
    public static class Logging {

        private boolean requestResponseEnabled = true;

        private long slowRequestThresholdMillis = 1000;

    }

    @Getter
    public static class Authorization {

        @Valid
        private final List<Rule> rules = new ArrayList<>();

    }

    @Getter
    @Setter
    public static class Internal {

        @NotNull(message = "gateway.internal.gateway-id is required")
        private String gatewayId;

        @NotNull(message = "gateway.internal.gateway-secret is required")
        private String gatewaySecret;

    }

    @Getter
    @Setter
    public static class Websocket {

        @NotNull(message = "gateway.websocket.trade-realtime-path is required")
        private String tradeRealtimePath = "/ws/trade/realtime";

        @NotNull(message = "gateway.websocket.trade-realtime-backend-path is required")
        private String tradeRealtimeBackendPath = "/ws/trade/realtime";

        @NotNull(message = "gateway.websocket.connect-timeout is required")
        private Duration connectTimeout = Duration.ofSeconds(3);

        private boolean forwardBrowserCookies;

    }

    @Getter
    @Setter
    public static class Rule {

        @NotNull(message = "gateway.authorization.rules[].path-pattern is required")
        private String pathPattern;

        private final List<String> requiredRoles = new ArrayList<>();

    }
}
