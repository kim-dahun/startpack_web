package com.upmudoum.erp.domain.route.repository;

import com.upmudoum.erp.domain.route.entity.Route;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {

    boolean existsByCode(String code);

    List<Route> findByEnabledTrueOrderByCodeAsc();

    List<Route> findByItemIdAndEnabledTrueOrderByCodeAsc(Long itemId);
}
