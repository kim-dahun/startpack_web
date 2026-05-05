package com.upmudoum.erp.domain.item.repository;

import com.upmudoum.erp.domain.item.entity.ItemCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Long> {

    boolean existsByCode(String code);

    Optional<ItemCategory> findByCode(String code);

    List<ItemCategory> findByActiveTrueOrderByDepthAscCodeAsc();
}
