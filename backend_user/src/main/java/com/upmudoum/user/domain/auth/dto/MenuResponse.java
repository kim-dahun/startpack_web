package com.upmudoum.user.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {

    private String menuId;
    private String parentMenuId;
    private String menuName;
    private String menuUrl;
    private String i18nCode;
    private String icon;
    private int menuLevel;
    private int sortSeq;
}
