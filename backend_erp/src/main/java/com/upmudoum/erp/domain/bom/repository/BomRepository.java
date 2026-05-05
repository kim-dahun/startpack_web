package com.upmudoum.erp.domain.bom.repository;

import com.upmudoum.erp.domain.bom.entity.Bom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BomRepository extends JpaRepository<Bom, Long> {

    Optional<Bom> findByParentItemIdAndEnabledTrue(Long parentItemId);
}
