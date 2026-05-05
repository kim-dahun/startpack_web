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
public class OpenApiTokenRequest {

    @NotBlank(message = "clientId is required.")
    private String clientId;

    @NotBlank(message = "clientSecret is required.")
    private String clientSecret;

    @NotBlank(message = "subject is required.")
    private String subject;

}
