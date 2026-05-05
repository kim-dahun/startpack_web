package com.upmudoum.erp.domain.production.repository;

import com.upmudoum.erp.domain.production.entity.ProductionConsumption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionConsumptionRepository extends JpaRepository<ProductionConsumption, Long> {

    List<ProductionConsumption> findByProductionResultId(Long productionResultId);
}
