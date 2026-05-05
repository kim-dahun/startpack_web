package com.upmudoum.trade.domain.master.repository;

import com.upmudoum.trade.domain.master.entity.TradeMasterImportHistory;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeMasterImportHistoryRepository extends JpaRepository<TradeMasterImportHistory, Long> {

    List<TradeMasterImportHistory> findTop50ByOrderByStartedAtDesc();

    List<TradeMasterImportHistory> findTop50ByMasterTypeOrderByStartedAtDesc(TradeMasterType masterType);

    java.util.Optional<TradeMasterImportHistory> findTopByMasterTypeOrderByStartedAtDesc(TradeMasterType masterType);
}
