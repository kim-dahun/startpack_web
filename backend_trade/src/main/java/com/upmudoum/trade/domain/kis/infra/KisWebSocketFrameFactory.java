package com.upmudoum.trade.domain.kis.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.trade.domain.kis.vo.KisRealtimeTransactionId;
import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeSubscriptionDto;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class KisWebSocketFrameFactory {

    private final ObjectMapper objectMapper;

    public KisWebSocketFrameFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String subscribe(String approvalKey, TradeRealtimeSubscriptionDto subscription) {
        return frame(approvalKey, "1", subscription);
    }

    public String unsubscribe(String approvalKey, TradeRealtimeSubscriptionDto subscription) {
        return frame(approvalKey, "2", subscription);
    }

    private String frame(String approvalKey, String tradeType, TradeRealtimeSubscriptionDto subscription) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "header", Map.of(
                            "approval_key", approvalKey,
                            "custtype", "P",
                            "tr_type", tradeType,
                            "content-type", "utf-8"
                    ),
                    "body", Map.of(
                            "input", Map.of(
                                    "tr_id", KisRealtimeTransactionId.from(subscription.getType()),
                                    "tr_key", subscription.getItemCode()
                            )
                    )
            ));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("failed to create KIS websocket frame", ex);
        }
    }
}
