package com.upmudoum.erp.domain.cost.repository;

import com.upmudoum.erp.domain.cost.entity.ItemStandardCost;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemStandardCostRepository extends JpaRepository<ItemStandardCost, Long> {

    List<ItemStandardCost> findByItemIdAndEnabledTrue(Long itemId);
}
