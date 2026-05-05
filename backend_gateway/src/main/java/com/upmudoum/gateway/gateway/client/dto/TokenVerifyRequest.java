package com.upmudoum.gateway.gateway.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenVerifyRequest {

    private String token;
    private TokenType tokenType;
}
