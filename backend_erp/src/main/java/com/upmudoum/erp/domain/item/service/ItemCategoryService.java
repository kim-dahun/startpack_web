package com.upmudoum.erp.domain.item.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.item.dto.ItemCategoryAssignRequest;
import com.upmudoum.erp.domain.item.dto.ItemCategoryAssignmentResponse;
import com.upmudoum.erp.domain.item.dto.ItemCategoryRequest;
import com.upmudoum.erp.domain.item.dto.ItemCategoryResponse;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.item.entity.ItemCategory;
import com.upmudoum.erp.domain.item.entity.ItemCategoryAssignment;
import com.upmudoum.erp.domain.item.repository.ItemCategoryAssignmentRepository;
import com.upmudoum.erp.domain.item.repository.ItemCategoryRepository;
import com.upmudoum.erp.domain.item.repository.ItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ItemCategoryService {

    private final ItemCategoryRepository categoryRepository;
    private final ItemCategoryAssignmentRepository assignmentRepository;
    private final ItemRepository itemRepository;

    public ItemCategoryService(ItemCategoryRepository categoryRepository,
                               ItemCategoryAssignmentRepository assignmentRepository, ItemRepository itemRepository) {
        this.categoryRepository = categoryRepository;
        this.assignmentRepository = assignmentRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public ItemCategoryResponse create(ItemCategoryRequest request) {
        if (categoryRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Item category code already exists");
        }
        ItemCategory parent = request.getParentCategoryId() == null ? null
                : categoryRepository.findById(request.getParentCategoryId())
                .orElseThrow(() -> new BusinessException("Parent category not found"));
        if (parent != null && parent.getDepth() >= 3) {
            throw new BusinessException("Item category depth cannot exceed 3");
        }
        return ItemCategoryResponse.from(categoryRepository.save(new ItemCategory(request.getCode(), request.getName(), parent)));
    }

    public List<ItemCategoryResponse> findAll() {
        return categoryRepository.findByActiveTrueOrderByDepthAscCodeAsc().stream()
                .map(ItemCategoryResponse::from)
                .toList();
    }

    @Transactional
    public ItemCategoryAssignmentResponse assign(ItemCategoryAssignRequest request) {
        if (assignmentRepository.existsByItemIdAndCategoryId(request.getItemId(), request.getCategoryId())) {
            throw new BusinessException("Item category assignment already exists");
        }
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(() -> new BusinessException("Item not found"));
        ItemCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException("Item category not found"));
        return ItemCategoryAssignmentResponse.from(assignmentRepository.save(new ItemCategoryAssignment(item, category)));
    }

    public List<ItemCategoryAssignmentResponse> findAssignmentsByItem(Long itemId) {
        return assignmentRepository.findByItemId(itemId).stream()
                .map(ItemCategoryAssignmentResponse::from)
                .toList();
    }

    public List<ItemCategoryAssignmentResponse> findAssignmentsByCategory(Long categoryId) {
        return assignmentRepository.findByCategoryId(categoryId).stream()
                .map(ItemCategoryAssignmentResponse::from)
                .toList();
    }
}
