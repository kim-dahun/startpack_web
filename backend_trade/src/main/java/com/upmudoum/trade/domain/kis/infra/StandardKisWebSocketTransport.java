package com.upmudoum.trade.domain.kis.infra;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@ConditionalOnProperty(name = "trade.kis.realtime-client.enabled", havingValue = "true")
public class StandardKisWebSocketTransport implements KisWebSocketTransport {

    private final StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
    private WebSocketSession session;

    @Override
    public void connect(String webSocketUrl, Consumer<String> messageConsumer) {
        try {
            this.session = webSocketClient.execute(new TextWebSocketHandler() {
                @Override
                protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                    messageConsumer.accept(message.getPayload());
                }
            }, webSocketUrl).get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while connecting KIS websocket", ex);
        } catch (ExecutionException ex) {
            throw new IllegalStateException("failed to connect KIS websocket", ex);
        }
    }

    @Override
    public void send(String message) {
        try {
            if (session == null || !session.isOpen()) {
                throw new IllegalStateException("KIS websocket is not open");
            }
            session.sendMessage(new TextMessage(message));
        } catch (java.io.IOException ex) {
            throw new IllegalStateException("failed to send KIS websocket message", ex);
        }
    }

    @Override
    public void close() {
        try {
            if (session != null) {
                session.close();
            }
        } catch (java.io.IOException ex) {
            throw new IllegalStateException("failed to close KIS websocket", ex);
        } finally {
            session = null;
        }
    }

    @Override
    public boolean isOpen() {
        return session != null && session.isOpen();
    }
}
