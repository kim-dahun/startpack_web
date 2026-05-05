package com.upmudoum.gateway.gateway.route;

import com.upmudoum.gateway.config.GatewayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class GatewayRouteConfiguration {

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes(GatewayProperties properties) {
        GatewayProperties.Services services = properties.getRoutes().getServices();

        return route()
                .route(path("/api/auth/**"), http(services.getAuthUrl()))
                .route(path("/api/v1/auth/**"), http(services.getAuthUrl()))
                .route(path("/api/users/**"), http(services.getUserUrl()))
                .route(path("/api/erp/**"), http(services.getErpUrl()))
                .route(path("/api/groupware/**"), http(services.getGroupwareUrl()))
                .route(path("/api/trade/**"), http(services.getTradeUrl()))
                .build();
    }
}
