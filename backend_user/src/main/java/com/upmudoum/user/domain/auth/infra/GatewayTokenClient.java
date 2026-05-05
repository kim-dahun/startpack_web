package com.upmudoum.user.domain.auth.infra;

import com.upmudoum.user.common.exception.BusinessException;
import com.upmudoum.user.common.exception.ErrorCode;
import com.upmudoum.user.common.response.ApiResponse;
import com.upmudoum.user.config.GatewayClientProperties;
import com.upmudoum.user.domain.auth.dto.GatewayTokenResponse;
import com.upmudoum.user.domain.auth.dto.LoginGroupResponse;
import com.upmudoum.user.domain.auth.vo.GatewayTokenIssueResult;
import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Component
public class GatewayTokenClient {

    private static final Logger log = LoggerFactory.getLogger(GatewayTokenClient.class);

    private final GatewayClientProperties properties;
    private final RestClient restClient;

    public GatewayTokenClient(GatewayClientProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClient = restClientBuilder.build();
    }

    public GatewayTokenIssueResult issueUserToken(
            String comCd,
            String userId,
            String loginId,
            String serviceId,
            List<String> serviceAccesses,
            List<LoginGroupResponse> groups,
            List<String> roles
    ) {
        URI uri = UriComponentsBuilder.fromUri(properties.getBaseUrl())
                .path(properties.getAuthLoginPath())
                .build()
                .toUri();

        GatewayLoginRequest request = new GatewayLoginRequest(comCd, userId, loginId, serviceId, serviceAccesses, groups, roles);
        log.info("login auth-token-request start comCd={} userId={} serviceId={} groups={} roles={}",
                comCd, userId, serviceId, groups.size(), roles.size());
        ResponseEntity<ApiResponse<GatewayTokenResponse>> entity = restClient.post()
                .uri(uri)
                .header("X-Internal-Gateway-Id", properties.getInternalGatewayId())
                .header("X-Internal-Gateway-Secret", properties.getInternalGatewaySecret())
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        ApiResponse<GatewayTokenResponse> response = entity.getBody();
        if (response == null || !response.isSuccess()) {
            log.warn("login auth-token-request failed comCd={} userId={} serviceId={} status={}",
                    comCd, userId, serviceId, entity.getStatusCode().value());
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Gateway token issue failed.");
        }
        List<String> setCookieHeaders = entity.getHeaders().getOrEmpty(HttpHeaders.SET_COOKIE);
        log.info("login auth-token-request success comCd={} userId={} serviceId={} cookies={}",
                comCd, userId, serviceId, setCookieHeaders.size());
        return new GatewayTokenIssueResult(response.getData(), setCookieHeaders);
    }

    @Getter
    @AllArgsConstructor
    private static class GatewayLoginRequest {

        private final String comCd;
        private final String userId;
        private final String loginId;
        private final String serviceId;
        private final List<String> serviceAccesses;
        private final List<LoginGroupResponse> groups;
        private final List<String> roles;
    }
}
