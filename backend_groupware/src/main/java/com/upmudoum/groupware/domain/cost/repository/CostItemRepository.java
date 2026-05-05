package com.upmudoum.groupware.domain.cost.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upmudoum.groupware.domain.cost.entity.CostItem;

public interface CostItemRepository extends JpaRepository<CostItem, UUID> {

    List<CostItem> findByComCdAndEnabledTrueAndDeletedYnFalseOrderByCostItemNameAsc(String comCd);

    Optional<CostItem> findByIdAndComCdAndDeletedYnFalse(UUID id, String comCd);
}
