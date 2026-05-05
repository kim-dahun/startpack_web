package com.upmudoum.erp.domain.batch.repository;

import com.upmudoum.erp.domain.batch.entity.ErpBatchDefinition;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErpBatchDefinitionRepository extends JpaRepository<ErpBatchDefinition, Long> {

    Optional<ErpBatchDefinition> findByCode(String code);
}
