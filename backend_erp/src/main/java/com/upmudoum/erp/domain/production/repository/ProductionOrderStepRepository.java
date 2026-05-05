package com.upmudoum.erp.domain.production.repository;

import com.upmudoum.erp.domain.production.entity.ProductionOrderStep;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionOrderStepRepository extends JpaRepository<ProductionOrderStep, Long> {

    List<ProductionOrderStep> findByProductionOrderIdOrderBySequenceNoAscIdAsc(Long productionOrderId);
}
