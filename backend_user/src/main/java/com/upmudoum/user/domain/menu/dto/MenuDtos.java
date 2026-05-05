package com.upmudoum.user.domain.menu.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public final class MenuDtos {

    private MenuDtos() {
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuRequest {
        private String comCd;
        private String serviceId;
        private String menuId;
        private String menuParentId;
        private String menuName;
        private String menuUrl;
        private String i18nCode;
        private String icon;
        private int menuLevel;
        private int sortSeq;
        private boolean enabled;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuResponse {
        private String comCd;
        private String serviceId;
        private String menuId;
        private String menuParentId;
        private String menuName;
        private String menuUrl;
        private String i18nCode;
        private String icon;
        private int menuLevel;
        private int sortSeq;
        private boolean enabled;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuTreeResponse {
        private String comCd;
        private String serviceId;
        private String menuId;
        private String menuParentId;
        private String menuName;
        private String menuUrl;
        private String i18nCode;
        private String icon;
        private int menuLevel;
        private int sortSeq;
        private boolean enabled;
        private List<MenuTreeResponse> children;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuPermissionRequest {
        private String comCd;
        private String serviceId;
        private String groupId;
        private String menuId;
        private boolean permitRead;
        private boolean permitWrite;
        private boolean permitDelete;
        private boolean permitExcel;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuPermissionResponse {
        private String comCd;
        private String serviceId;
        private String groupId;
        private String menuId;
        private boolean permitRead;
        private boolean permitWrite;
        private boolean permitDelete;
        private boolean permitExcel;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuPermissionTreeResponse {
        private String comCd;
        private String serviceId;
        private String groupId;
        private String menuId;
        private String menuParentId;
        private String menuName;
        private boolean permitRead;
        private boolean permitWrite;
        private boolean permitDelete;
        private boolean permitExcel;
        private List<MenuPermissionTreeResponse> children;
    }
}
