package com.upmudoum.erp.domain.item.dto;

import com.upmudoum.erp.domain.item.entity.ItemCategoryAssignment;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemCategoryAssignmentResponse {

    private Long id;
    private Long itemId;
    private String itemCode;
    private Long categoryId;
    private String categoryCode;
    private String categoryName;

    public static ItemCategoryAssignmentResponse from(ItemCategoryAssignment assignment) {
        ItemCategoryAssignmentResponse response = new ItemCategoryAssignmentResponse();
        response.id = assignment.getId();
        response.itemId = assignment.getItem().getId();
        response.itemCode = assignment.getItem().getCode().getValue();
        response.categoryId = assignment.getCategory().getId();
        response.categoryCode = assignment.getCategory().getCode();
        response.categoryName = assignment.getCategory().getName();
        return response;
    }
}
