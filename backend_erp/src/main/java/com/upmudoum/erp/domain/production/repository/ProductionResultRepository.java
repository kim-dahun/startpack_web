package com.upmudoum.erp.domain.production.repository;

import com.upmudoum.erp.domain.production.entity.ProductionResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionResultRepository extends JpaRepository<ProductionResult, Long> {

    List<ProductionResult> findByProductionOrderIdOrderByCompletedAtDescIdDesc(Long productionOrderId);
}
