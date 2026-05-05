package com.upmudoum.trade.domain.account.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisteredAccountDto {

    private Long id;
    private String accountNo;
    private String accountName;
    private String productCode;
    private String aliasName;
    private String memo;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
