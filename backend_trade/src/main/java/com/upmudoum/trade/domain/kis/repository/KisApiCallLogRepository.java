package com.upmudoum.trade.domain.kis.repository;

import com.upmudoum.trade.domain.kis.entity.KisApiCallLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KisApiCallLogRepository extends JpaRepository<KisApiCallLog, Long> {

    List<KisApiCallLog> findTop50ByOrderByCalledAtDesc();
}
