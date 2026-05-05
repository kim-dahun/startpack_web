package com.upmudoum.groupware.domain.directory.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDirectoryItem {

    private String userId;
    private String userName;
    private String jobGradeId;
    private String jobGradeName;
    private String primaryDepartmentId;
    private String primaryDepartmentName;
    private String primaryPositionId;
    private String primaryPositionName;
    private List<UserDirectoryAffiliation> affiliations;
    private String source;

    public UserDirectoryItem(String userId, String source) {
        this.userId = userId;
        this.source = source;
    }
}
