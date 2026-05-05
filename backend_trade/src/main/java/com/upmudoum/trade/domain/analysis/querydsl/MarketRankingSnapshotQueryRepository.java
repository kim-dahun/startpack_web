package com.upmudoum.trade.domain.analysis.querydsl;

import static com.upmudoum.trade.domain.analysis.entity.QMarketRankingSnapshot.marketRankingSnapshot;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.upmudoum.trade.domain.analysis.dto.RankingItemDto;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class MarketRankingSnapshotQueryRepository {

    private final JPAQueryFactory queryFactory;

    public MarketRankingSnapshotQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<RankingItemDto> rankSnapshots(LocalDate baseDate, String rankingType, TradeMasterType masterType, int limit) {
        NumberExpression<BigDecimal> metric = snapshotMetric(rankingType);
        return queryFactory
                .select(Projections.bean(
                        RankingItemDto.class,
                        marketRankingSnapshot.itemCode,
                        marketRankingSnapshot.itemName,
                        marketRankingSnapshot.marketCode,
                        marketRankingSnapshot.countryCode,
                        marketRankingSnapshot.sectorName,
                        metric.as("metricValue")
                ))
                .from(marketRankingSnapshot)
                .where(baseDateEq(baseDate), masterTypeEq(masterType))
                .orderBy(order(metric, rankingType))
                .limit(Math.max(1, Math.min(limit, 200)))
                .fetch();
    }

    private BooleanExpression baseDateEq(LocalDate baseDate) {
        return marketRankingSnapshot.baseDate.eq(baseDate);
    }

    private BooleanExpression masterTypeEq(TradeMasterType masterType) {
        return masterType == null ? null : marketRankingSnapshot.masterType.eq(masterType);
    }

    private NumberExpression<BigDecimal> snapshotMetric(String rankingType) {
        return switch (rankingType) {
            case "volume" -> marketRankingSnapshot.volume.coalesce(BigDecimal.ZERO);
            case "turnover" -> marketRankingSnapshot.turnover.coalesce(BigDecimal.ZERO);
            case "gainers", "losers" -> marketRankingSnapshot.changeRate.coalesce(BigDecimal.ZERO);
            case "high52" -> marketRankingSnapshot.high52WeekPrice.coalesce(BigDecimal.ZERO);
            case "low52" -> marketRankingSnapshot.low52WeekPrice.coalesce(BigDecimal.ZERO);
            case "volatility" -> marketRankingSnapshot.volatility.coalesce(BigDecimal.ZERO);
            default -> marketRankingSnapshot.marketCap.coalesce(BigDecimal.ZERO);
        };
    }

    private OrderSpecifier<BigDecimal> order(NumberExpression<BigDecimal> metric, String rankingType) {
        if ("losers".equals(rankingType) || "low52".equals(rankingType)) {
            return metric.asc();
        }
        return metric.desc();
    }
}
