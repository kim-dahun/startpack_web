package com.upmudoum.erp.domain.item.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.item.dto.ItemCategoryAssignRequest;
import com.upmudoum.erp.domain.item.dto.ItemCategoryAssignmentResponse;
import com.upmudoum.erp.domain.item.dto.ItemCategoryRequest;
import com.upmudoum.erp.domain.item.dto.ItemCategoryResponse;
import com.upmudoum.erp.domain.item.service.ItemCategoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/erp/item-categories")
public class ItemCategoryController {

    private final ItemCategoryService categoryService;

    public ItemCategoryController(ItemCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ApiResponse<ItemCategoryResponse> create(@Valid @RequestBody ItemCategoryRequest request) {
        return ApiResponse.ok(categoryService.create(request));
    }

    @GetMapping
    public ApiResponse<List<ItemCategoryResponse>> findAll() {
        return ApiResponse.ok(categoryService.findAll());
    }

    @PostMapping("/assignments")
    public ApiResponse<ItemCategoryAssignmentResponse> assign(@Valid @RequestBody ItemCategoryAssignRequest request) {
        return ApiResponse.ok(categoryService.assign(request));
    }

    @GetMapping("/assignments")
    public ApiResponse<List<ItemCategoryAssignmentResponse>> findAssignments(@RequestParam(required = false) Long itemId,
                                                                             @RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return ApiResponse.ok(categoryService.findAssignmentsByCategory(categoryId));
        }
        return ApiResponse.ok(categoryService.findAssignmentsByItem(itemId));
    }
}
