package com.upmudoum.groupware.domain.directory.infra;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BackendUserResponse {

    private String comCd;
    private String userId;
    private String userName;
    private String email;
    private String phone;
    private String address;
    private String jobGradeId;
    private String status;
}
