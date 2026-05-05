package com.upmudoum.erp.domain.production.repository;

import com.upmudoum.erp.domain.production.entity.ProductionResultStep;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionResultStepRepository extends JpaRepository<ProductionResultStep, Long> {

    List<ProductionResultStep> findByProductionResultIdOrderBySequenceNoAscIdAsc(Long productionResultId);
}
