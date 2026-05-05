package com.upmudoum.erp.domain.inventory.repository;

import com.upmudoum.erp.domain.inventory.entity.InventoryMovementLot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementLotRepository extends JpaRepository<InventoryMovementLot, Long> {

    List<InventoryMovementLot> findByMovementId(Long movementId);
}
