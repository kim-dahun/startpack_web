package com.upmudoum.auth.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginGroupRequest {

    @NotBlank(message = "group comCd is required.")
    private String comCd;

    @NotBlank(message = "group serviceId is required.")
    private String serviceId;

    @NotBlank(message = "groupId is required.")
    private String groupId;

    private String groupName;
}
