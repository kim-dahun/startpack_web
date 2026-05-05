package com.upmudoum.gateway.gateway.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AuthRefreshResponse {

    private final String accessToken;
    private final List<String> setCookieHeaders;
}
