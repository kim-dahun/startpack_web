package com.upmudoum.user.domain.auth.service;

import com.upmudoum.user.common.exception.BusinessException;
import com.upmudoum.user.common.exception.ErrorCode;
import com.upmudoum.user.domain.auth.dto.LoginGroupResponse;
import com.upmudoum.user.domain.auth.dto.LoginInitResponse;
import com.upmudoum.user.domain.auth.dto.LoginRequest;
import com.upmudoum.user.domain.auth.dto.MenuPermissionResponse;
import com.upmudoum.user.domain.auth.dto.MenuResponse;
import com.upmudoum.user.domain.auth.dto.SignupRequest;
import com.upmudoum.user.domain.auth.dto.UserSummaryResponse;
import com.upmudoum.user.domain.auth.infra.GatewayTokenClient;
import com.upmudoum.user.domain.auth.vo.GatewayTokenIssueResult;
import com.upmudoum.user.domain.auth.vo.LoginResult;
import com.upmudoum.user.domain.group.UserGroup;
import com.upmudoum.user.domain.group.UserGroupMember;
import com.upmudoum.user.domain.group.UserGroupMemberRepository;
import com.upmudoum.user.domain.group.UserGroupRepository;
import com.upmudoum.user.domain.menu.Menu;
import com.upmudoum.user.domain.menu.MenuPermission;
import com.upmudoum.user.domain.menu.MenuPermissionRepository;
import com.upmudoum.user.domain.menu.MenuRepository;
import com.upmudoum.user.domain.serviceaccess.ServiceId;
import com.upmudoum.user.domain.serviceaccess.UserServiceAccessRepository;
import com.upmudoum.user.domain.user.UserAccount;
import com.upmudoum.user.domain.user.UserAccountRepository;
import com.upmudoum.user.domain.user.UserStatus;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAuthService {

    private static final Logger log = LoggerFactory.getLogger(UserAuthService.class);

    private final UserAccountRepository userAccountRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserGroupMemberRepository userGroupMemberRepository;
    private final MenuRepository menuRepository;
    private final MenuPermissionRepository menuPermissionRepository;
    private final UserServiceAccessRepository serviceAccessRepository;
    private final PasswordEncoder passwordEncoder;
    private final GatewayTokenClient gatewayTokenClient;

    public UserAuthService(
            UserAccountRepository userAccountRepository,
            UserGroupRepository userGroupRepository,
            UserGroupMemberRepository userGroupMemberRepository,
            MenuRepository menuRepository,
            MenuPermissionRepository menuPermissionRepository,
            UserServiceAccessRepository serviceAccessRepository,
            PasswordEncoder passwordEncoder,
            GatewayTokenClient gatewayTokenClient
    ) {
        this.userAccountRepository = userAccountRepository;
        this.userGroupRepository = userGroupRepository;
        this.userGroupMemberRepository = userGroupMemberRepository;
        this.menuRepository = menuRepository;
        this.menuPermissionRepository = menuPermissionRepository;
        this.serviceAccessRepository = serviceAccessRepository;
        this.passwordEncoder = passwordEncoder;
        this.gatewayTokenClient = gatewayTokenClient;
    }

    @Transactional
    public UserSummaryResponse signup(SignupRequest request) {
        if (userAccountRepository.existsByComCdAndUserId(request.getComCd(), request.getUserId())) {
            throw new BusinessException(ErrorCode.CONFLICT, "User already exists.");
        }
        UserAccount user = new UserAccount(
                request.getComCd(),
                request.getUserId(),
                request.getUserName(),
                passwordEncoder.encode(request.getPassword())
        );
        user.updateProfile(request.getUserName(), request.getEmail(), request.getPhone(), request.getAddress(), UserStatus.ACTIVE);
        return toUserSummary(userAccountRepository.save(user));
    }

    @Transactional
    public LoginResult login(LoginRequest request) {
        log.info("login start comCd={} userId={} serviceId={}", request.getComCd(), request.getUserId(), request.getServiceId());
        UserAccount user = validateCredentials(request.getComCd(), request.getUserId(), request.getPassword());
        log.info("login user-validated comCd={} userId={} status={}", request.getComCd(), user.getUserId(), user.getStatus());
        ServiceId requestedServiceId = requireServiceAccess(request.getComCd(), user.getUserId(), request.getServiceId());
        log.info("login service-access-validated comCd={} userId={} serviceId={}", request.getComCd(), user.getUserId(), requestedServiceId.name());
        List<String> serviceAccesses = serviceAccesses(request.getComCd(), user.getUserId());

        List<UserGroupMember> groupMembers = userGroupMemberRepository.findByComCdAndServiceIdAndUserId(request.getComCd(), requestedServiceId.name(), request.getUserId());
        List<String> groupIds = groupMembers.stream().map(UserGroupMember::getGroupId).toList();
        List<LoginGroupResponse> groups = groupMembers.stream()
                .map(this::toLoginGroupResponse)
                .toList();
        log.info("login groups-loaded comCd={} userId={} serviceId={} groups={}",
                request.getComCd(), user.getUserId(), requestedServiceId.name(), groups.size());
        List<MenuResponse> menus = menuRepository.findByComCdAndServiceIdAndEnabledOrderByDepthAscSortOrderAsc(request.getComCd(), requestedServiceId.name(), true)
                .stream()
                .map(this::toMenuResponse)
                .toList();
        List<MenuPermissionResponse> permissions = mergePermissions(request.getComCd(), requestedServiceId.name(), groupIds);
        log.info("login init-data-loaded comCd={} userId={} serviceId={} menus={} menuPermissions={} serviceAccesses={}",
                request.getComCd(), user.getUserId(), requestedServiceId.name(), menus.size(), permissions.size(), serviceAccesses.size());

        GatewayTokenIssueResult tokenIssueResult = gatewayTokenClient.issueUserToken(
                request.getComCd(),
                user.getUserId(),
                user.getUserId(),
                requestedServiceId.name(),
                serviceAccesses,
                groups,
                groupIds
        );
        user.markLoggedIn();
        log.info("login complete comCd={} userId={} serviceId={}", request.getComCd(), user.getUserId(), requestedServiceId.name());

        LoginInitResponse loginInitResponse = new LoginInitResponse(toUserSummary(user), requestedServiceId.name(), serviceAccesses, tokenIssueResult.getToken(), groups, menus, permissions);
        return new LoginResult(loginInitResponse, tokenIssueResult.getSetCookieHeaders());
    }

    private UserAccount validateCredentials(String comCd, String userId, String password) {
        UserAccount user = userAccountRepository.findByComCdAndUserId(comCd, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid user or password."));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "User status is not active.");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid user or password.");
        }
        return user;
    }

    private ServiceId requireServiceAccess(String comCd, String userId, String serviceIdValue) {
        ServiceId serviceId = toServiceId(serviceIdValue);
        boolean accessible = serviceAccessRepository.findByComCdAndUserIdAndServiceId(comCd, userId, serviceId)
                .map(access -> access.isAccessible())
                .orElse(false);
        if (!accessible) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "User cannot access service.");
        }
        return serviceId;
    }

    private List<String> serviceAccesses(String comCd, String userId) {
        return serviceAccessRepository.findByComCdAndUserIdAndAccessibleOrderByServiceIdAsc(comCd, userId, true)
                .stream()
                .map(access -> access.getServiceId().name())
                .toList();
    }

    private ServiceId toServiceId(String value) {
        try {
            return ServiceId.from(value);
        } catch (RuntimeException ex) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Unsupported serviceId.");
        }
    }

    private List<MenuPermissionResponse> mergePermissions(String comCd, String serviceId, List<String> groupIds) {
        if (groupIds.isEmpty()) {
            return List.of();
        }
        Map<String, PermissionAccumulator> merged = new LinkedHashMap<>();
        for (MenuPermission permission : menuPermissionRepository.findByComCdAndServiceIdAndGroupIdIn(comCd, serviceId, groupIds)) {
            merged.computeIfAbsent(permission.getMenuId(), PermissionAccumulator::new).merge(permission);
        }
        return new ArrayList<>(merged.values()).stream()
                .map(PermissionAccumulator::toResponse)
                .toList();
    }

    private LoginGroupResponse toLoginGroupResponse(UserGroupMember member) {
        UserGroup group = userGroupRepository.findByComCdAndServiceIdAndGroupId(member.getComCd(), member.getServiceId(), member.getGroupId()).orElse(null);
        return new LoginGroupResponse(
                member.getComCd(),
                member.getServiceId(),
                member.getGroupId(),
                group == null ? null : group.getGroupName()
        );
    }

    private MenuResponse toMenuResponse(Menu menu) {
        return new MenuResponse(
                menu.getMenuId(),
                menu.getParentMenuId(),
                menu.getMenuName(),
                menu.getPath(),
                menu.getI18nCode(),
                menu.getIcon(),
                menu.getDepth(),
                menu.getSortOrder()
        );
    }

    private UserSummaryResponse toUserSummary(UserAccount user) {
        return new UserSummaryResponse(
                user.getComCd(),
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getStatus().name()
        );
    }

    private static class PermissionAccumulator {
        private final String menuId;
        private boolean readable;
        private boolean writable;
        private boolean deletable;
        private boolean excelDownable;

        PermissionAccumulator(String menuId) {
            this.menuId = menuId;
        }

        void merge(MenuPermission permission) {
            this.readable = this.readable || permission.isReadable();
            this.writable = this.writable || permission.isWritable();
            this.deletable = this.deletable || permission.isDeletable();
            this.excelDownable = this.excelDownable || permission.isExcelDownable();
        }

        MenuPermissionResponse toResponse() {
            return new MenuPermissionResponse(menuId, readable, writable, deletable, excelDownable);
        }
    }
}
