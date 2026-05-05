package com.upmudoum.trade.domain.analysis.service;

import com.upmudoum.trade.domain.analysis.dto.MarketGroupStrengthDto;
import com.upmudoum.trade.domain.analysis.dto.MarketRankingSnapshotDto;
import com.upmudoum.trade.domain.analysis.dto.RankingItemDto;
import com.upmudoum.trade.domain.analysis.dto.SaveMarketRankingSnapshotsRequest;
import com.upmudoum.trade.domain.analysis.entity.MarketRankingSnapshot;
import com.upmudoum.trade.domain.analysis.querydsl.MarketRankingSnapshotQueryRepository;
import com.upmudoum.trade.domain.analysis.repository.MarketRankingSnapshotRepository;
import com.upmudoum.trade.domain.item.querydsl.ItemMasterQueryRepository;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarketAnalysisService {

    private final ItemMasterQueryRepository itemMasterQueryRepository;
    private final MarketRankingSnapshotRepository snapshotRepository;
    private final MarketRankingSnapshotQueryRepository snapshotQueryRepository;

    public MarketAnalysisService(
            ItemMasterQueryRepository itemMasterQueryRepository,
            MarketRankingSnapshotRepository snapshotRepository,
            MarketRankingSnapshotQueryRepository snapshotQueryRepository
    ) {
        this.itemMasterQueryRepository = itemMasterQueryRepository;
        this.snapshotRepository = snapshotRepository;
        this.snapshotQueryRepository = snapshotQueryRepository;
    }

    @Transactional(readOnly = true)
    public List<RankingItemDto> ranking(String rankingType, TradeMasterType masterType) {
        LocalDate today = LocalDate.now();
        List<RankingItemDto> rankings;
        if (snapshotRepository.existsByBaseDate(today)) {
            rankings = snapshotQueryRepository.rankSnapshots(today, rankingType, masterType, 50);
        } else {
            rankings = itemMasterQueryRepository.rankFromMaster(rankingType, masterType, 50);
        }
        AtomicInteger rank = new AtomicInteger(1);
        return rankings.stream()
                .peek(item -> item.setRank(rank.getAndIncrement()))
                .toList();
    }

    @Transactional
    public int saveSnapshots(SaveMarketRankingSnapshotsRequest request) {
        return persistSnapshots(request);
    }

    @Transactional
    public int replaceSnapshots(SaveMarketRankingSnapshotsRequest request) {
        snapshotRepository.deleteByBaseDate(request.getBaseDate());
        return persistSnapshots(request);
    }

    private int persistSnapshots(SaveMarketRankingSnapshotsRequest request) {
        List<MarketRankingSnapshot> snapshots = request.getSnapshots().stream()
                .map(snapshot -> toEntity(request.getBaseDate(), snapshot))
                .toList();
        snapshotRepository.saveAll(snapshots);
        return snapshots.size();
    }

    public List<MarketGroupStrengthDto> sectors() {
        return itemMasterQueryRepository.sectorStrengths();
    }

    public List<MarketGroupStrengthDto> themes() {
        return itemMasterQueryRepository.themeStrengths();
    }

    private MarketRankingSnapshot toEntity(LocalDate baseDate, MarketRankingSnapshotDto dto) {
        MarketRankingSnapshot snapshot = new MarketRankingSnapshot();
        snapshot.setBaseDate(baseDate);
        snapshot.setMasterType(dto.getMasterType());
        snapshot.setItemCode(dto.getItemCode());
        snapshot.setItemName(dto.getItemName());
        snapshot.setMarketCode(dto.getMarketCode());
        snapshot.setCountryCode(dto.getCountryCode());
        snapshot.setSectorName(dto.getSectorName());
        snapshot.setCurrentPrice(dto.getCurrentPrice());
        snapshot.setChangeAmount(dto.getChangeAmount());
        snapshot.setChangeRate(dto.getChangeRate());
        snapshot.setVolume(dto.getVolume());
        snapshot.setTurnover(dto.getTurnover());
        snapshot.setMarketCap(dto.getMarketCap());
        snapshot.setHigh52WeekPrice(dto.getHigh52WeekPrice());
        snapshot.setLow52WeekPrice(dto.getLow52WeekPrice());
        snapshot.setVolatility(dto.getVolatility());
        snapshot.setCapturedAt(Instant.now());
        return snapshot;
    }

}
