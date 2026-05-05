package com.upmudoum.trade.domain.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KisOpenApiTokenRequest {

    @JsonProperty("grant_type")
    private String grantType = "client_credentials";

    @JsonProperty("appkey")
    private String appKey;

    @JsonProperty("appsecret")
    private String appSecret;

    public KisOpenApiTokenRequest(String appKey, String appSecret) {
        this.appKey = appKey;
        this.appSecret = appSecret;
    }
}
