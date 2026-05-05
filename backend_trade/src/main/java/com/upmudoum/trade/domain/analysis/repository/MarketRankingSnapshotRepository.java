package com.upmudoum.trade.domain.analysis.repository;

import com.upmudoum.trade.domain.analysis.entity.MarketRankingSnapshot;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketRankingSnapshotRepository extends JpaRepository<MarketRankingSnapshot, Long> {

    List<MarketRankingSnapshot> findByBaseDateOrderByMarketCapDesc(LocalDate baseDate);

    List<MarketRankingSnapshot> findByBaseDateAndMasterTypeOrderByMarketCapDesc(LocalDate baseDate, TradeMasterType masterType);

    boolean existsByBaseDate(LocalDate baseDate);

    void deleteByBaseDate(LocalDate baseDate);
}
