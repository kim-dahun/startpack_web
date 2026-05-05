package com.upmudoum.erp.domain.bom.repository;

import com.upmudoum.erp.domain.bom.entity.BomComponent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BomComponentRepository extends JpaRepository<BomComponent, Long> {

    List<BomComponent> findByBomVersionId(Long bomVersionId);
}
