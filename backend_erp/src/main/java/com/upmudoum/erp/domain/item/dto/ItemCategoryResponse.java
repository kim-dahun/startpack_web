package com.upmudoum.erp.domain.item.dto;

import com.upmudoum.erp.domain.item.entity.ItemCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemCategoryResponse {

    private Long id;
    private String code;
    private String name;
    private Long parentCategoryId;
    private int depth;
    private boolean active;

    public static ItemCategoryResponse from(ItemCategory category) {
        ItemCategoryResponse response = new ItemCategoryResponse();
        response.id = category.getId();
        response.code = category.getCode();
        response.name = category.getName();
        response.parentCategoryId = category.getParentCategory() == null ? null : category.getParentCategory().getId();
        response.depth = category.getDepth();
        response.active = category.isActive();
        return response;
    }
}
