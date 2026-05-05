package com.upmudoum.trade.domain.kis.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KisWebSocketApprovalRequest {

    private String grant_type;
    private String appkey;
    private String secretkey;

    public KisWebSocketApprovalRequest(String appkey, String secretkey) {
        this.grant_type = "client_credentials";
        this.appkey = appkey;
        this.secretkey = secretkey;
    }
}
