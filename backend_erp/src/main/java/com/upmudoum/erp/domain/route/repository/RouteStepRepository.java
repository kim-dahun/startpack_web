package com.upmudoum.erp.domain.route.repository;

import com.upmudoum.erp.domain.route.entity.RouteStep;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteStepRepository extends JpaRepository<RouteStep, Long> {

    boolean existsByRouteIdAndSequenceNoAndEnabledTrue(Long routeId, Integer sequenceNo);

    List<RouteStep> findByRouteIdAndEnabledTrueOrderBySequenceNoAscIdAsc(Long routeId);
}
