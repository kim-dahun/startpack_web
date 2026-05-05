package com.upmudoum.user.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.upmudoum.user.common.exception.BusinessException;
import com.upmudoum.user.domain.auth.dto.GatewayTokenResponse;
import com.upmudoum.user.domain.auth.dto.LoginRequest;
import com.upmudoum.user.domain.auth.infra.GatewayTokenClient;
import com.upmudoum.user.domain.auth.vo.LoginResult;
import com.upmudoum.user.domain.auth.vo.GatewayTokenIssueResult;
import com.upmudoum.user.domain.group.UserGroup;
import com.upmudoum.user.domain.group.UserGroupMember;
import com.upmudoum.user.domain.group.UserGroupMemberRepository;
import com.upmudoum.user.domain.group.UserGroupRepository;
import com.upmudoum.user.domain.menu.MenuPermissionRepository;
import com.upmudoum.user.domain.menu.MenuRepository;
import com.upmudoum.user.domain.serviceaccess.ServiceId;
import com.upmudoum.user.domain.serviceaccess.UserServiceAccess;
import com.upmudoum.user.domain.serviceaccess.UserServiceAccessRepository;
import com.upmudoum.user.domain.user.UserAccount;
import com.upmudoum.user.domain.user.UserAccountRepository;
import com.upmudoum.user.domain.user.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserAuthServiceTest {

    private UserAccountRepository userAccountRepository;
    private UserGroupRepository userGroupRepository;
    private UserGroupMemberRepository userGroupMemberRepository;
    private MenuRepository menuRepository;
    private MenuPermissionRepository menuPermissionRepository;
    private UserServiceAccessRepository serviceAccessRepository;
    private PasswordEncoder passwordEncoder;
    private GatewayTokenClient gatewayTokenClient;
    private UserAuthService userAuthService;

    @BeforeEach
    void setUp() {
        userAccountRepository = mock(UserAccountRepository.class);
        userGroupRepository = mock(UserGroupRepository.class);
        userGroupMemberRepository = mock(UserGroupMemberRepository.class);
        menuRepository = mock(MenuRepository.class);
        menuPermissionRepository = mock(MenuPermissionRepository.class);
        serviceAccessRepository = mock(UserServiceAccessRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        gatewayTokenClient = mock(GatewayTokenClient.class);
        userAuthService = new UserAuthService(
                userAccountRepository,
                userGroupRepository,
                userGroupMemberRepository,
                menuRepository,
                menuPermissionRepository,
                serviceAccessRepository,
                passwordEncoder,
                gatewayTokenClient
        );
    }

    @Test
    void loginRejectsUserWithoutRequestedServiceAccess() {
        UserAccount user = new UserAccount("COM001", "user01", "User", "encoded");
        user.updateProfile("User", "user@example.com", null, null, UserStatus.ACTIVE);
        when(userAccountRepository.findByComCdAndUserId("COM001", "user01")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);
        when(serviceAccessRepository.findByComCdAndUserIdAndServiceId("COM001", "user01", ServiceId.ERP))
                .thenReturn(Optional.of(new UserServiceAccess("COM001", "user01", ServiceId.ERP, false)));

        LoginRequest request = new LoginRequest("COM001", "user01", "secret", "ERP");

        assertThatThrownBy(() -> userAuthService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User cannot access service.");
        verifyNoInteractions(gatewayTokenClient);
    }

    @Test
    void loginReturnsGroupsForRequestedService() {
        UserAccount user = new UserAccount("COM001", "admin01", "Admin", "encoded");
        user.updateProfile("Admin", "admin@example.com", null, null, UserStatus.ACTIVE);
        UserServiceAccess access = new UserServiceAccess("COM001", "admin01", ServiceId.ERP, true);
        UserGroupMember adminMember = new UserGroupMember("COM001", "ERP", "ADMIN", "admin01");
        UserGroup adminGroup = new UserGroup("COM001", "ERP", "ADMIN", "관리자");
        GatewayTokenResponse token = new GatewayTokenResponse("admin01", "admin01", Instant.now(), Instant.now(), List.of("ADMIN"), "HTTP_ONLY_COOKIE");

        when(userAccountRepository.findByComCdAndUserId("COM001", "admin01")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);
        when(serviceAccessRepository.findByComCdAndUserIdAndServiceId("COM001", "admin01", ServiceId.ERP)).thenReturn(Optional.of(access));
        when(serviceAccessRepository.findByComCdAndUserIdAndAccessibleOrderByServiceIdAsc("COM001", "admin01", true)).thenReturn(List.of(access));
        when(gatewayTokenClient.issueUserToken(eq("COM001"), eq("admin01"), eq("admin01"), eq("ERP"), anyList(), anyList(), anyList()))
                .thenReturn(new GatewayTokenIssueResult(token, List.of("ACCESS_TOKEN=access; HttpOnly", "REFRESH_TOKEN=refresh; HttpOnly")));
        when(userGroupMemberRepository.findByComCdAndServiceIdAndUserId("COM001", "ERP", "admin01")).thenReturn(List.of(adminMember));
        when(userGroupRepository.findByComCdAndServiceIdAndGroupId("COM001", "ERP", "ADMIN")).thenReturn(Optional.of(adminGroup));
        when(menuRepository.findByComCdAndServiceIdAndEnabledOrderByDepthAscSortOrderAsc("COM001", "ERP", true)).thenReturn(List.of());
        when(menuPermissionRepository.findByComCdAndServiceIdAndGroupIdIn("COM001", "ERP", List.of("ADMIN"))).thenReturn(List.of());

        LoginResult result = userAuthService.login(new LoginRequest("COM001", "admin01", "secret", "ERP"));

        assertThat(result.getResponse().getGroups()).hasSize(1);
        assertThat(result.getResponse().getGroups().get(0).getComCd()).isEqualTo("COM001");
        assertThat(result.getResponse().getGroups().get(0).getServiceId()).isEqualTo("ERP");
        assertThat(result.getResponse().getGroups().get(0).getGroupId()).isEqualTo("ADMIN");
        assertThat(result.getResponse().getGroups().get(0).getGroupName()).isEqualTo("관리자");
        assertThat(result.getSetCookieHeaders())
                .containsExactly("ACCESS_TOKEN=access; HttpOnly", "REFRESH_TOKEN=refresh; HttpOnly");
        verify(gatewayTokenClient).issueUserToken(eq("COM001"), eq("admin01"), eq("admin01"), eq("ERP"), eq(List.of("ERP")), anyList(), eq(List.of("ADMIN")));
    }
}
