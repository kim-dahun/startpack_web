package com.upmudoum.erp.domain.warehouse.repository;

import com.upmudoum.erp.domain.warehouse.entity.WarehouseSafetyStock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseSafetyStockRepository extends JpaRepository<WarehouseSafetyStock, Long> {

    Optional<WarehouseSafetyStock> findByItemIdAndWarehouseId(Long itemId, Long warehouseId);

    List<WarehouseSafetyStock> findByWarehouseIdAndActiveTrueOrderByItemCodeValueAsc(Long warehouseId);
}
