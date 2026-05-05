package com.upmudoum.groupware.domain.cost.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CostItemRequest {

    @NotBlank
    private String costItemName;
    private boolean enabled = true;
}
