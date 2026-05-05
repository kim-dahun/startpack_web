package com.upmudoum.groupware.domain.directory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDirectoryAffiliation {

    private String departmentId;
    private String departmentName;
    private String positionId;
    private String positionName;
    private boolean primaryYn;
}
