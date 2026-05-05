package com.upmudoum.groupware.domain.cost.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upmudoum.groupware.domain.cost.entity.ScheduleCost;

public interface ScheduleCostRepository extends JpaRepository<ScheduleCost, UUID> {

    List<ScheduleCost> findByComCdAndScheduleIdAndDeletedYnFalseOrderByCostDateDesc(String comCd, UUID scheduleId);

    List<ScheduleCost> findByComCdAndProjectIdAndDeletedYnFalseOrderByCostDateDesc(String comCd, UUID projectId);

    List<ScheduleCost> findByComCdAndProjectCodeAndDeletedYnFalseOrderByCostDateDesc(String comCd, String projectCode);

    Optional<ScheduleCost> findByIdAndComCdAndDeletedYnFalse(UUID id, String comCd);

    List<ScheduleCost> findByComCdAndDeletedYnFalseOrderByCostDateDesc(String comCd);
}
