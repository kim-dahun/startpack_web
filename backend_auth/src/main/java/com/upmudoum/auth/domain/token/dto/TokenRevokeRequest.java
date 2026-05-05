package com.upmudoum.auth.domain.token.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenRevokeRequest {

    @NotBlank(message = "refreshToken is required.")
    private String refreshToken;

}
