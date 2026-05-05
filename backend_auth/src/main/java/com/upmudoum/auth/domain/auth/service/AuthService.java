package com.upmudoum.auth.domain.auth.service;

import com.upmudoum.auth.domain.audit.service.AuthAuditService;
import com.upmudoum.auth.domain.audit.vo.AuthAuditAction;
import com.upmudoum.auth.domain.audit.vo.AuthAuditOutcome;
import com.upmudoum.auth.domain.auth.dto.LoginGroupRequest;
import com.upmudoum.auth.domain.auth.dto.LoginRequest;
import com.upmudoum.auth.domain.auth.dto.OpenApiTokenRequest;
import com.upmudoum.auth.domain.auth.dto.OpenApiTokenResponse;
import com.upmudoum.auth.domain.auth.repository.OpenApiClientRepository;
import com.upmudoum.auth.domain.auth.vo.LoginResult;
import com.upmudoum.auth.domain.auth.vo.OpenApiPrincipal;
import com.upmudoum.auth.domain.auth.vo.UserGroupSummary;
import com.upmudoum.auth.domain.token.service.RefreshTokenStore;
import com.upmudoum.auth.domain.token.vo.TokenIssueResult;
import com.upmudoum.auth.domain.token.infra.JwtTokenProvider;
import com.upmudoum.auth.exception.ApiException;
import com.upmudoum.auth.exception.ErrorCode;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final OpenApiClientRepository openApiClientRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final AuthAuditService authAuditService;

    public AuthService(
            OpenApiClientRepository openApiClientRepository,
            JwtTokenProvider jwtTokenProvider,
            RefreshTokenStore refreshTokenStore,
            AuthAuditService authAuditService
    ) {
        this.openApiClientRepository = openApiClientRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenStore = refreshTokenStore;
        this.authAuditService = authAuditService;
    }

    @Transactional
    public LoginResult login(LoginRequest request) {
        try {
            if (request.getServiceAccesses() == null || !request.getServiceAccesses().contains(request.getServiceId())) {
                throw new ApiException(ErrorCode.SERVICE_ACCESS_DENIED);
            }

            List<UserGroupSummary> groups = request.getGroups() == null ? List.of() : request.getGroups().stream()
                    .map(this::toUserGroupSummary)
                    .toList();

            TokenIssueResult accessToken = jwtTokenProvider.issueAccessToken(request.getUserId(), request.getRoles());
            TokenIssueResult refreshToken = jwtTokenProvider.issueRefreshToken(request.getUserId(), request.getRoles());
            refreshTokenStore.save(refreshToken);
            authAuditService.record(AuthAuditAction.LOGIN, AuthAuditOutcome.SUCCESS, request.getUserId(), "OK", "login success");

            return LoginResult.builder()
                    .userId(request.getUserId())
                    .loginId(request.getLoginId())
                    .serviceId(request.getServiceId())
                    .serviceAccesses(request.getServiceAccesses())
                    .groups(groups)
                    .accessToken(accessToken.getToken())
                    .accessTokenExpiresAt(accessToken.getExpiresAt())
                    .refreshToken(refreshToken.getToken())
                    .refreshTokenExpiresAt(refreshToken.getExpiresAt())
                    .roles(request.getRoles())
                    .build();
        } catch (ApiException exception) {
            authAuditService.record(AuthAuditAction.LOGIN, AuthAuditOutcome.FAILURE, request.getLoginId(), exception.getErrorCode().code(), "login failed");
            throw exception;
        }
    }

    public OpenApiTokenResponse issueOpenApiToken(OpenApiTokenRequest request) {
        try {
            OpenApiPrincipal principal = openApiClientRepository.findByClientIdAndEnabledTrue(request.getClientId())
                    .filter(client -> client.matches(request.getClientSecret(), request.getSubject()))
                    .map(client -> OpenApiPrincipal.builder()
                            .subject(client.getSubject())
                            .clientId(client.getClientId())
                            .scopes(client.scopes())
                            .build())
                    .orElseThrow(() -> new ApiException(ErrorCode.INVALID_OPEN_API_CLIENT));

            TokenIssueResult token = jwtTokenProvider.issueOpenApiToken(principal.getSubject(), principal.getScopes());
            authAuditService.record(AuthAuditAction.OPEN_API_TOKEN_ISSUE, AuthAuditOutcome.SUCCESS, principal.getSubject(), "OK", request.getClientId());
            return OpenApiTokenResponse.builder()
                    .subject(principal.getSubject())
                    .clientId(principal.getClientId())
                    .accessToken(token.getToken())
                    .accessTokenExpiresAt(token.getExpiresAt())
                    .scopes(principal.getScopes())
                    .build();
        } catch (ApiException exception) {
            authAuditService.record(AuthAuditAction.OPEN_API_TOKEN_ISSUE, AuthAuditOutcome.FAILURE, request.getSubject(), exception.getErrorCode().code(), request.getClientId());
            throw exception;
        }
    }

    private UserGroupSummary toUserGroupSummary(LoginGroupRequest group) {
        return UserGroupSummary.builder()
                .comCd(group.getComCd())
                .serviceId(group.getServiceId())
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .build();
    }
}
