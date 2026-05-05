package com.upmudoum.user.domain.auth.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginInitResponse {

    private UserSummaryResponse user;
    private String serviceId;
    private List<String> serviceAccesses;
    private GatewayTokenResponse token;
    private List<LoginGroupResponse> groups;
    private List<MenuResponse> menus;
    private List<MenuPermissionResponse> menuPermissions;
}
