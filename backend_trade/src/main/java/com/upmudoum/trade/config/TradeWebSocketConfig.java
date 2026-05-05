package com.upmudoum.trade.config;

import com.upmudoum.trade.domain.marketdata.controller.TradeRealtimeWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class TradeWebSocketConfig implements WebSocketConfigurer {

    private final TradeRealtimeWebSocketHandler realtimeWebSocketHandler;
    private final GatewayOnlyHandshakeInterceptor gatewayOnlyHandshakeInterceptor;

    public TradeWebSocketConfig(
            TradeRealtimeWebSocketHandler realtimeWebSocketHandler,
            GatewayOnlyHandshakeInterceptor gatewayOnlyHandshakeInterceptor
    ) {
        this.realtimeWebSocketHandler = realtimeWebSocketHandler;
        this.gatewayOnlyHandshakeInterceptor = gatewayOnlyHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(realtimeWebSocketHandler, "/ws/trade/realtime")
                .addInterceptors(gatewayOnlyHandshakeInterceptor);
    }
}
