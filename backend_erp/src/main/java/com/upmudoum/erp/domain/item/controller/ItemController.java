package com.upmudoum.erp.domain.item.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.item.dto.ItemRequest;
import com.upmudoum.erp.domain.item.dto.ItemResponse;
import com.upmudoum.erp.domain.item.service.ItemService;
import com.upmudoum.erp.domain.item.vo.ItemType;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/erp/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ApiResponse<ItemResponse> create(@Valid @RequestBody ItemRequest request) {
        return ApiResponse.ok(itemService.create(request));
    }

    @GetMapping
    public ApiResponse<List<ItemResponse>> findAll() {
        return ApiResponse.ok(itemService.findAll());
    }

    @GetMapping("/search")
    public ApiResponse<List<ItemResponse>> search(@RequestParam(required = false) ItemType itemType,
                                                  @RequestParam(required = false) Boolean active,
                                                  @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(itemService.search(itemType, active, keyword));
    }

    @PutMapping("/{id}")
    public ApiResponse<ItemResponse> update(@PathVariable Long id, @Valid @RequestBody ItemRequest request) {
        return ApiResponse.ok(itemService.update(id, request));
    }
}
