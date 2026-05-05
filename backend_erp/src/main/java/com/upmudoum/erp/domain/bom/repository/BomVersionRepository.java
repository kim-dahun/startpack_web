package com.upmudoum.erp.domain.bom.repository;

import com.upmudoum.erp.domain.bom.entity.BomVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BomVersionRepository extends JpaRepository<BomVersion, Long> {

    List<BomVersion> findByBomParentItemIdAndEnabledTrueOrderByEffectiveFromDescIdDesc(Long parentItemId);
}
