package com.upmudoum.gateway.gateway.websocket;

import com.upmudoum.gateway.common.GatewayHeaders;
import com.upmudoum.gateway.config.GatewayProperties;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class TradeRealtimeWebSocketBridgeHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(TradeRealtimeWebSocketBridgeHandler.class);
    private static final String BACKEND_SESSION_ATTRIBUTE = "tradeBackendWebSocketSession";

    private final StandardWebSocketClient webSocketClient;
    private final TradeWebSocketBridgeRequestFactory requestFactory;
    private final GatewayProperties properties;

    @Autowired
    public TradeRealtimeWebSocketBridgeHandler(
            TradeWebSocketBridgeRequestFactory requestFactory,
            GatewayProperties properties
    ) {
        this(new StandardWebSocketClient(), requestFactory, properties);
    }

    TradeRealtimeWebSocketBridgeHandler(
            StandardWebSocketClient webSocketClient,
            TradeWebSocketBridgeRequestFactory requestFactory,
            GatewayProperties properties
    ) {
        this.webSocketClient = webSocketClient;
        this.requestFactory = requestFactory;
        this.properties = properties;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession frontendSession) throws Exception {
        URI backendUri = requestFactory.backendUri(frontendSession.getUri());
        WebSocketHttpHeaders backendHeaders = requestFactory.backendHeaders(frontendSession.getHandshakeHeaders());
        BackendBridgeHandler backendHandler = new BackendBridgeHandler(frontendSession);
        WebSocketSession backendSession;
        try {
            backendSession = webSocketClient.execute(backendHandler, backendHeaders, backendUri)
                    .get(properties.getWebsocket().getConnectTimeout().toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            closeFrontend(frontendSession, CloseStatus.SERVER_ERROR);
            return;
        } catch (ExecutionException | TimeoutException ex) {
            log.warn("gateway websocket backend connect failure path={} backend={} requestId={}",
                    properties.getWebsocket().getTradeRealtimePath(), backendUri,
                    frontendSession.getHandshakeHeaders().getFirst(GatewayHeaders.REQUEST_ID));
            closeFrontend(frontendSession, CloseStatus.SERVER_ERROR);
            return;
        }
        frontendSession.getAttributes().put(BACKEND_SESSION_ATTRIBUTE, backendSession);
        log.info("gateway websocket bridge connected path={} backend={} requestId={}",
                properties.getWebsocket().getTradeRealtimePath(), backendUri,
                frontendSession.getHandshakeHeaders().getFirst(GatewayHeaders.REQUEST_ID));
    }

    @Override
    protected void handleTextMessage(WebSocketSession frontendSession, TextMessage message) throws Exception {
        WebSocketSession backendSession = backendSession(frontendSession);
        if (backendSession == null || !backendSession.isOpen()) {
            closeFrontend(frontendSession, CloseStatus.SERVICE_RESTARTED);
            return;
        }
        send(backendSession, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession frontendSession, CloseStatus status) throws Exception {
        closeBackend(frontendSession, status);
    }

    @Override
    public void handleTransportError(WebSocketSession frontendSession, Throwable exception) throws Exception {
        closeBackend(frontendSession, CloseStatus.SERVER_ERROR);
    }

    private WebSocketSession backendSession(WebSocketSession frontendSession) {
        Object backendSession = frontendSession.getAttributes().get(BACKEND_SESSION_ATTRIBUTE);
        if (backendSession instanceof WebSocketSession webSocketSession) {
            return webSocketSession;
        }
        return null;
    }

    private void closeBackend(WebSocketSession frontendSession, CloseStatus status) throws Exception {
        WebSocketSession backendSession = backendSession(frontendSession);
        if (backendSession != null && backendSession.isOpen()) {
            backendSession.close(status);
        }
    }

    private void closeFrontend(WebSocketSession frontendSession, CloseStatus status) throws Exception {
        if (frontendSession.isOpen()) {
            frontendSession.close(status);
        }
    }

    private void send(WebSocketSession session, TextMessage message) throws Exception {
        synchronized (session) {
            if (session.isOpen()) {
                session.sendMessage(message);
            }
        }
    }

    private class BackendBridgeHandler extends TextWebSocketHandler {

        private final WebSocketSession frontendSession;

        BackendBridgeHandler(WebSocketSession frontendSession) {
            this.frontendSession = frontendSession;
        }

        @Override
        protected void handleTextMessage(WebSocketSession backendSession, TextMessage message) throws Exception {
            if (frontendSession.isOpen()) {
                send(frontendSession, message);
            }
        }

        @Override
        public void afterConnectionClosed(WebSocketSession backendSession, CloseStatus status) throws Exception {
            closeFrontend(frontendSession, status);
        }

        @Override
        public void handleTransportError(WebSocketSession backendSession, Throwable exception) throws Exception {
            closeFrontend(frontendSession, CloseStatus.SERVER_ERROR);
        }
    }
}
