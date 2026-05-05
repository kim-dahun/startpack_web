package com.upmudoum.user.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuPermissionResponse {

    private String menuId;
    private boolean permitRead;
    private boolean permitWrite;
    private boolean permitDelete;
    private boolean permitExcel;
}
