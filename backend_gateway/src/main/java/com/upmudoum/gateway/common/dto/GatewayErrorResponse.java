package com.upmudoum.gateway.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GatewayErrorResponse {

    private String code;
    private String message;
    private String requestId;
    private Instant timestamp;
}
