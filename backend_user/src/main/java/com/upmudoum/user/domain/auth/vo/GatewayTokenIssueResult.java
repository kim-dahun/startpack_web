package com.upmudoum.user.domain.auth.vo;

import com.upmudoum.user.domain.auth.dto.GatewayTokenResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GatewayTokenIssueResult {

    private GatewayTokenResponse token;
    private List<String> setCookieHeaders;
}
