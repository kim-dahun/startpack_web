package com.upmudoum.trade.domain.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterAccountRequest {

    @NotBlank
    private String accountNo;

    @NotBlank
    private String accountName;

    private String productCode;

    private String aliasName;

    private String memo;

    private Boolean active;
}
