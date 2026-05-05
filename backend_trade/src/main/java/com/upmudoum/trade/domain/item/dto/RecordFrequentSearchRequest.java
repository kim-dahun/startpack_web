package com.upmudoum.trade.domain.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecordFrequentSearchRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String itemCode;

    @NotBlank
    private String itemName;

    @NotBlank
    private String marketCode;
}
