package com.upmudoum.erp.domain.equipment.repository;

import com.upmudoum.erp.domain.equipment.entity.EquipmentProcess;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentProcessRepository extends JpaRepository<EquipmentProcess, Long> {

    boolean existsByEquipmentIdAndProcessIdAndEnabledTrue(Long equipmentId, Long processId);

    List<EquipmentProcess> findByEquipmentIdAndEnabledTrueOrderByIdAsc(Long equipmentId);
}
