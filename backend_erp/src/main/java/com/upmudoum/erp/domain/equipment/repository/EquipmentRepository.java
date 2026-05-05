package com.upmudoum.erp.domain.equipment.repository;

import com.upmudoum.erp.domain.equipment.entity.Equipment;
import com.upmudoum.erp.domain.equipment.vo.EquipmentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    boolean existsByCode(String code);

    List<Equipment> findByEnabledTrueOrderByCodeAsc();

    List<Equipment> findByEquipmentTypeAndStatusAndEnabledTrueOrderByCodeAsc(String equipmentType, EquipmentStatus status);
}
