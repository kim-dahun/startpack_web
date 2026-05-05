package com.upmudoum.erp.domain.inventory.repository;

import com.upmudoum.erp.domain.inventory.entity.InventoryBalance;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryBalanceRepository extends JpaRepository<InventoryBalance, Long> {

    Optional<InventoryBalance> findByItemIdAndWarehouseId(Long itemId, Long warehouseId);
}
