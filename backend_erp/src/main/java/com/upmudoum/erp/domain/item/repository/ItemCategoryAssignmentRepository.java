package com.upmudoum.erp.domain.item.repository;

import com.upmudoum.erp.domain.item.entity.ItemCategoryAssignment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCategoryAssignmentRepository extends JpaRepository<ItemCategoryAssignment, Long> {

    boolean existsByItemIdAndCategoryId(Long itemId, Long categoryId);

    List<ItemCategoryAssignment> findByItemId(Long itemId);

    List<ItemCategoryAssignment> findByCategoryId(Long categoryId);
}
