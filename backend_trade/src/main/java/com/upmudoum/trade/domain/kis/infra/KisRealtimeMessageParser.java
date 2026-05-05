package com.upmudoum.trade.domain.kis.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.trade.domain.kis.vo.KisRealtimeTransactionId;
import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeEventDto;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class KisRealtimeMessageParser {

    private final ObjectMapper objectMapper;

    public KisRealtimeMessageParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Optional<TradeRealtimeEventDto> parse(String rawMessage) {
        if (rawMessage == null || rawMessage.isBlank()) {
            return Optional.empty();
        }
        String trimmed = rawMessage.trim();
        if (trimmed.startsWith("{")) {
            return parseJson(trimmed);
        }
        if (trimmed.contains("|")) {
            return parseDelimited(trimmed);
        }
        return Optional.empty();
    }

    private Optional<TradeRealtimeEventDto> parseJson(String rawMessage) {
        try {
            JsonNode root = objectMapper.readTree(rawMessage);
            String transactionId = firstText(root.at("/header/tr_id"), root.at("/header/trId"));
            if (transactionId == null) {
                return Optional.empty();
            }
            JsonNode output = outputNode(root);
            String itemCode = firstText(
                    output.path("stck_shrn_iscd"),
                    output.path("tr_key"),
                    output.path("pdno"),
                    output.path("itemCode")
            );
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("raw", rawMessage);
            payload.put("trId", transactionId);
            payload.put("output", objectMapper.convertValue(output, Map.class));
            return buildEvent(transactionId, itemCode, payload);
        } catch (IOException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private Optional<TradeRealtimeEventDto> parseDelimited(String rawMessage) {
        String[] parts = rawMessage.split("\\|", 4);
        if (parts.length < 3) {
            return Optional.empty();
        }
        String transactionId = parts[1];
        String payloadText = parts.length == 4 ? parts[3] : parts[2];
        List<String> fields = List.of(payloadText.split("\\^", -1));
        String itemCode = fields.isEmpty() ? null : fields.get(0);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("raw", rawMessage);
        payload.put("trId", transactionId);
        payload.put("fields", new ArrayList<>(fields));
        return buildEvent(transactionId, itemCode, payload);
    }

    private Optional<TradeRealtimeEventDto> buildEvent(String transactionId, String itemCode, Map<String, Object> payload) {
        try {
            TradeRealtimeEventDto event = new TradeRealtimeEventDto();
            event.setType(KisRealtimeTransactionId.eventTypeOf(transactionId));
            event.setItemCode(itemCode == null || itemCode.isBlank() ? "UNKNOWN" : itemCode.trim());
            event.setOccurredAt(Instant.now());
            event.setPayload(payload);
            return Optional.of(event);
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private JsonNode outputNode(JsonNode root) {
        JsonNode output = root.at("/body/output");
        if (!output.isMissingNode()) {
            return output;
        }
        JsonNode output1 = root.at("/body/output1");
        if (!output1.isMissingNode()) {
            return output1;
        }
        return root.path("body");
    }

    private String firstText(JsonNode... nodes) {
        for (JsonNode node : nodes) {
            if (node != null && !node.isMissingNode() && !node.isNull()) {
                String value = node.asText();
                if (value != null && !value.isBlank()) {
                    return value;
                }
            }
        }
        return null;
    }
}
