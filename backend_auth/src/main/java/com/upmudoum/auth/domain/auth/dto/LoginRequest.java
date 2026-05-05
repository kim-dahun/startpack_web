package com.upmudoum.auth.domain.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "comCd is required.")
    private String comCd;

    @NotBlank(message = "userId is required.")
    private String userId;

    @NotBlank(message = "loginId is required.")
    private String loginId;

    @NotBlank(message = "serviceId is required.")
    private String serviceId;

    @NotEmpty(message = "serviceAccesses is required.")
    private List<String> serviceAccesses;

    @Valid
    @NotNull(message = "groups is required.")
    private List<LoginGroupRequest> groups;

    @NotNull(message = "roles is required.")
    private List<String> roles;
}
