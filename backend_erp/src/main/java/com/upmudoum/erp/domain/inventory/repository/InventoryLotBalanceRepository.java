package com.upmudoum.erp.domain.inventory.repository;

import com.upmudoum.erp.domain.inventory.entity.InventoryLotBalance;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryLotBalanceRepository extends JpaRepository<InventoryLotBalance, Long> {

    Optional<InventoryLotBalance> findByItemIdAndWarehouseIdAndLotId(Long itemId, Long warehouseId, Long lotId);

    Optional<InventoryLotBalance> findByItemIdAndWarehouseIdAndLotIsNull(Long itemId, Long warehouseId);

    List<InventoryLotBalance> findByItemIdAndWarehouseIdOrderByFirstReceivedAtAscIdAsc(Long itemId, Long warehouseId);

    List<InventoryLotBalance> findByItemIdAndWarehouseIdAndQuantityValueGreaterThanOrderByFirstReceivedAtAscIdAsc(
            Long itemId, Long warehouseId, java.math.BigDecimal quantity);
}
