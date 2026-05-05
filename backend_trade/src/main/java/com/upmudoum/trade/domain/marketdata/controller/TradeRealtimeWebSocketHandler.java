package com.upmudoum.trade.domain.marketdata.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeSubscriptionDto;
import com.upmudoum.trade.domain.marketdata.service.TradeRealtimeService;
import com.upmudoum.trade.domain.marketdata.vo.TradeRealtimeEventType;
import java.io.IOException;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class TradeRealtimeWebSocketHandler extends TextWebSocketHandler {

    private final TradeRealtimeService realtimeService;
    private final ObjectMapper objectMapper;

    public TradeRealtimeWebSocketHandler(TradeRealtimeService realtimeService, ObjectMapper objectMapper) {
        this.realtimeService = realtimeService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        realtimeService.registerSession(session);
        sendAck(session, "connected", null);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode root = objectMapper.readTree(message.getPayload());
        String action = requiredText(root, "action");

        if ("ping".equalsIgnoreCase(action)) {
            sendAck(session, "pong", null);
            return;
        }

        TradeRealtimeSubscriptionDto subscription = new TradeRealtimeSubscriptionDto(
                TradeRealtimeEventType.valueOf(requiredText(root, "type").toUpperCase()),
                requiredText(root, "itemCode")
        );

        if ("subscribe".equalsIgnoreCase(action)) {
            realtimeService.subscribe(session, subscription);
            sendAck(session, "subscribed", subscription);
            return;
        }

        if ("unsubscribe".equalsIgnoreCase(action)) {
            realtimeService.unsubscribe(session, subscription);
            sendAck(session, "unsubscribed", subscription);
            return;
        }

        throw new IllegalArgumentException("unsupported action: " + action);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        realtimeService.removeSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        realtimeService.removeSession(session);
    }

    private String requiredText(JsonNode root, String fieldName) {
        JsonNode field = root.get(fieldName);
        if (field == null || field.asText().isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return field.asText();
    }

    private void sendAck(WebSocketSession session, String status, Object data) {
        realtimeService.send(session, Map.of("status", status, "data", data == null ? Map.of() : data));
    }
}
