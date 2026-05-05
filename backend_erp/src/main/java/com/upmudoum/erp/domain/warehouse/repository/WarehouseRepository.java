package com.upmudoum.erp.domain.warehouse.repository;

import com.upmudoum.erp.domain.warehouse.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    boolean existsByCodeValue(String code);
}
