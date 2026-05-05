package com.upmudoum.groupware.domain.directory.infra;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BackendUserApiResponse<T> {

    private boolean success;
    private T data;
    private String responseMessage;
    private Integer responseCode;
}
