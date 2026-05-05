package com.upmudoum.groupware.domain.directory.infra;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.upmudoum.groupware.common.infra.GatewayCredentialVerifier;

@Component
public class BackendUserDirectoryClient {

    private final boolean enabled;
    private final String organizationUsersUrl;
    private final String gatewayId;
    private final String gatewaySecret;
    private final RestClient restClient;

    public BackendUserDirectoryClient(
            @Value("${groupware.user-directory.enabled:true}") boolean enabled,
            @Value("${groupware.user-directory.base-url:http://localhost:9091}") String baseUrl,
            @Value("${groupware.user-directory.organization-users-path:/api/organization/users}") String organizationUsersPath,
            @Value("${groupware.gateway-client.gateway-id:backend_gateway}") String gatewayId,
            @Value("${groupware.gateway-client.gateway-secret:local-dev-gateway-secret}") String gatewaySecret) {
        this.enabled = enabled;
        this.organizationUsersUrl = baseUrl + organizationUsersPath;
        this.gatewayId = gatewayId;
        this.gatewaySecret = gatewaySecret;
        this.restClient = RestClient.builder().build();
    }

    public Optional<List<BackendUserOrganizationUserResponse>> searchOrganizationUsers(String comCd, String keyword) {
        if (!enabled) {
            return Optional.empty();
        }
        try {
            BackendUserApiResponse<List<BackendUserOrganizationUserResponse>> response = restClient.get()
                    .uri(UriComponentsBuilder.fromUriString(organizationUsersUrl)
                            .queryParam("comCd", comCd)
                            .queryParamIfPresent("keyword", Optional.ofNullable(blankToNull(keyword)))
                            .build()
                            .toUri())
                    .header(GatewayCredentialVerifier.GATEWAY_ID_HEADER, gatewayId)
                    .header(GatewayCredentialVerifier.GATEWAY_SECRET_HEADER, gatewaySecret)
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
            if (response == null || !response.isSuccess() || response.getData() == null) {
                return Optional.empty();
            }
            return Optional.of(response.getData());
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    public Optional<List<BackendUserOrganizationUserResponse>> searchOrganizationUsersByDepartment(
            String comCd,
            String departmentId) {
        if (!enabled) {
            return Optional.empty();
        }
        try {
            BackendUserApiResponse<List<BackendUserOrganizationUserResponse>> response = restClient.get()
                    .uri(UriComponentsBuilder.fromUriString(organizationUsersUrl)
                            .queryParam("comCd", comCd)
                            .queryParamIfPresent("departmentId", Optional.ofNullable(blankToNull(departmentId)))
                            .build()
                            .toUri())
                    .header(GatewayCredentialVerifier.GATEWAY_ID_HEADER, gatewayId)
                    .header(GatewayCredentialVerifier.GATEWAY_SECRET_HEADER, gatewaySecret)
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
            if (response == null || !response.isSuccess() || response.getData() == null) {
                return Optional.empty();
            }
            return Optional.of(response.getData());
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    public Optional<List<BackendUserResponse>> listUsers(String comCd) {
        if (!enabled) {
            return Optional.empty();
        }
        try {
            BackendUserApiResponse<List<BackendUserResponse>> response = restClient.get()
                    .uri(UriComponentsBuilder.fromUriString(organizationUsersUrl)
                            .replacePath("/api/users")
                            .queryParam("comCd", comCd)
                            .build()
                            .toUri())
                    .header(GatewayCredentialVerifier.GATEWAY_ID_HEADER, gatewayId)
                    .header(GatewayCredentialVerifier.GATEWAY_SECRET_HEADER, gatewaySecret)
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
            if (response == null || !response.isSuccess() || response.getData() == null) {
                return Optional.empty();
            }
            return Optional.of(response.getData());
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    public boolean isActiveUser(String comCd, String userId) {
        if (userId == null || userId.isBlank()) {
            return false;
        }
        Optional<List<BackendUserResponse>> users = listUsers(comCd);
        if (users.isEmpty()) {
            return true;
        }
        return users.get().stream()
                .filter(user -> userId.equals(user.getUserId()))
                .findFirst()
                .map(user -> "ACTIVE".equalsIgnoreCase(user.getStatus()))
                .orElse(false);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
