package com.upmudoum.groupware.domain.directory.infra;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BackendUserAffiliationResponse {

    private String comCd;
    private String userPositionId;
    private String departmentId;
    private String departmentName;
    private String positionId;
    private String positionName;
    private boolean primaryYn;
}
