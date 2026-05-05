package com.upmudoum.erp.domain.item.dto;

import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.item.vo.ItemType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemResponse {

    private Long id;
    private String code;
    private String name;
    private String unit;
    private ItemType itemType;
    private boolean active;

    public static ItemResponse from(Item item) {
        ItemResponse response = new ItemResponse();
        response.id = item.getId();
        response.code = item.getCode().getValue();
        response.name = item.getName();
        response.unit = item.getUnit();
        response.itemType = item.getItemType();
        response.active = item.isActive();
        return response;
    }

}
