package com.upmudoum.gateway.config;

import com.upmudoum.gateway.gateway.websocket.TradeRealtimeWebSocketBridgeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class GatewayWebSocketConfiguration implements WebSocketConfigurer {

    private final GatewayProperties properties;
    private final TradeRealtimeWebSocketBridgeHandler tradeRealtimeWebSocketBridgeHandler;

    public GatewayWebSocketConfiguration(
            GatewayProperties properties,
            TradeRealtimeWebSocketBridgeHandler tradeRealtimeWebSocketBridgeHandler
    ) {
        this.properties = properties;
        this.tradeRealtimeWebSocketBridgeHandler = tradeRealtimeWebSocketBridgeHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(
                        tradeRealtimeWebSocketBridgeHandler,
                        properties.getWebsocket().getTradeRealtimePath()
                )
                .setAllowedOrigins(properties.getCors().getAllowedOrigins().toArray(String[]::new));
    }
}
