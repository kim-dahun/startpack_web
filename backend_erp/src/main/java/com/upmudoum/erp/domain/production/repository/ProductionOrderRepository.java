package com.upmudoum.erp.domain.production.repository;

import com.upmudoum.erp.domain.production.entity.ProductionOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {

    List<ProductionOrder> findAllByOrderByDueDateDescIdDesc();
}
