package com.upmudoum.trade.config;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class GatewayOnlyHandshakeInterceptor implements HandshakeInterceptor {

    private final GatewayAccessProperties properties;

    public GatewayOnlyHandshakeInterceptor(GatewayAccessProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        String gatewayId = request.getHeaders().getFirst(properties.getGatewayIdHeaderName());
        String gatewaySecret = request.getHeaders().getFirst(properties.getGatewaySecretHeaderName());
        if (!properties.isAllowed(gatewayId, gatewaySecret)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }
        attributes.put("gatewayId", gatewayId);
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
    }
}
