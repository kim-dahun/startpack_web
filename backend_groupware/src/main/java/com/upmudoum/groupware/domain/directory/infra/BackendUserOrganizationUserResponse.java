package com.upmudoum.groupware.domain.directory.infra;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BackendUserOrganizationUserResponse {

    private String comCd;
    private String userId;
    private String userName;
    private String jobGradeId;
    private String jobGradeName;
    private List<BackendUserAffiliationResponse> affiliations;
}
