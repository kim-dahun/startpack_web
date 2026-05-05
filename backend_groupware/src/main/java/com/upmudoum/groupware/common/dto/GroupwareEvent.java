package com.upmudoum.groupware.common.dto;

import java.time.Instant;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupwareEvent {

    private String comCd;
    private String userId;
    private String channel;
    private String eventType;
    private Map<String, Object> payload;
    private Instant occurredAt;
}
