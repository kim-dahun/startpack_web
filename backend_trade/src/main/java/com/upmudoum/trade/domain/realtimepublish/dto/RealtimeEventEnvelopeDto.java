package com.upmudoum.trade.domain.realtimepublish.dto;

import com.upmudoum.trade.domain.realtimepublish.vo.RealtimeEventType;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RealtimeEventEnvelopeDto {

    private RealtimeEventType eventType;
    private String topic;
    private String itemCode;
    private String accountNo;
    private Instant occurredAt;
    private long sequenceNo;
    private Map<String, Object> payload;
}
