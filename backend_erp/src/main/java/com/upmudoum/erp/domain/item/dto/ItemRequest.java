package com.upmudoum.erp.domain.item.dto;

import com.upmudoum.erp.domain.item.vo.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRequest {

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 20)
    private String unit;

    private ItemType itemType = ItemType.RAW_MATERIAL;

    private boolean active = true;

}
