package com.upmudoum.erp.domain.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemCategoryAssignRequest {

    @NotNull
    private Long itemId;

    @NotNull
    private Long categoryId;
}
