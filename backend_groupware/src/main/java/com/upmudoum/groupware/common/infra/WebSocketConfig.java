package com.upmudoum.groupware.common.infra;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String GATEWAY_VERIFIED_ATTRIBUTE = "gatewayVerified";

    private final boolean gatewayOnlyEnabled;
    private final GatewayCredentialVerifier gatewayCredentialVerifier;

    public WebSocketConfig(
            @Value("${groupware.gateway-only.enabled:true}") boolean gatewayOnlyEnabled,
            GatewayCredentialVerifier gatewayCredentialVerifier) {
        this.gatewayOnlyEnabled = gatewayOnlyEnabled;
        this.gatewayCredentialVerifier = gatewayCredentialVerifier;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/groupware")
                .addInterceptors(gatewayHandshakeInterceptor());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                if (!gatewayOnlyEnabled) {
                    return message;
                }
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if (StompCommand.CONNECT.equals(accessor.getCommand())
                        && !Boolean.TRUE.equals(sessionAttributes(accessor).get(GATEWAY_VERIFIED_ATTRIBUTE))) {
                    throw new MessageDeliveryException("WebSocket requests must pass through gateway");
                }
                return message;
            }
        });
    }

    private HandshakeInterceptor gatewayHandshakeInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(
                    org.springframework.http.server.ServerHttpRequest request,
                    org.springframework.http.server.ServerHttpResponse response,
                    WebSocketHandler wsHandler,
                    Map<String, Object> attributes) {
                if (!gatewayOnlyEnabled) {
                    attributes.put(GATEWAY_VERIFIED_ATTRIBUTE, true);
                    return true;
                }
                String gatewayId = request.getHeaders().getFirst(GatewayCredentialVerifier.GATEWAY_ID_HEADER);
                String gatewaySecret = request.getHeaders().getFirst(GatewayCredentialVerifier.GATEWAY_SECRET_HEADER);
                boolean verified = gatewayCredentialVerifier.isAllowed(gatewayId, gatewaySecret);
                attributes.put(GATEWAY_VERIFIED_ATTRIBUTE, verified);
                return verified;
            }

            @Override
            public void afterHandshake(
                    org.springframework.http.server.ServerHttpRequest request,
                    org.springframework.http.server.ServerHttpResponse response,
                    WebSocketHandler wsHandler,
                    Exception exception) {
            }
        };
    }

    private Map<String, Object> sessionAttributes(StompHeaderAccessor accessor) {
        Map<String, Object> attributes = accessor.getSessionAttributes();
        return attributes == null ? Map.of() : attributes;
    }
}
