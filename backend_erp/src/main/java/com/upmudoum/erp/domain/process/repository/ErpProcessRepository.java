package com.upmudoum.erp.domain.process.repository;

import com.upmudoum.erp.domain.process.entity.ErpProcess;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErpProcessRepository extends JpaRepository<ErpProcess, Long> {

    boolean existsByCode(String code);

    List<ErpProcess> findByEnabledTrueOrderByCodeAsc();

    List<ErpProcess> findByProcessTypeAndEnabledTrueOrderByCodeAsc(String processType);
}
