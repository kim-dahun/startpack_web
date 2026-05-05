package com.upmudoum.erp.domain.inventory.repository;

import com.upmudoum.erp.domain.inventory.entity.InventoryMovement;
import com.upmudoum.erp.domain.inventory.vo.InventoryReferenceType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

    List<InventoryMovement> findByItemIdAndWarehouseIdOrderByOccurredAtDesc(Long itemId, Long warehouseId);

    List<InventoryMovement> findByReferenceTypeAndReferenceIdOrderByOccurredAtAscIdAsc(
            InventoryReferenceType referenceType, Long referenceId);
}
