package com.upmudoum.user.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank
    private String comCd;

    @NotBlank
    private String userId;

    @NotBlank
    private String password;

    @NotBlank
    private String serviceId;

    public LoginRequest(String comCd, String userId, String password, String serviceId) {
        this.comCd = comCd;
        this.userId = userId;
        this.password = password;
        this.serviceId = serviceId;
    }
}
