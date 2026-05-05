package com.upmudoum.user.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {

    @NotBlank
    private String comCd;

    @NotBlank
    private String userId;

    @NotBlank
    private String password;

    @NotBlank
    private String userName;

    private String email;
    private String phone;
    private String address;

    public SignupRequest(String comCd, String userId, String password, String userName, String email, String phone, String address) {
        this.comCd = comCd;
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
}
