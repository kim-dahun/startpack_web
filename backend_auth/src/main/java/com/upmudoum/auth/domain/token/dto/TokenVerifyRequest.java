package com.upmudoum.auth.domain.token.dto;

import com.upmudoum.auth.domain.token.vo.TokenType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenVerifyRequest {

    @NotBlank(message = "token is required.")
    private String token;

    @NotNull(message = "tokenType is required.")
    private TokenType tokenType;

}
