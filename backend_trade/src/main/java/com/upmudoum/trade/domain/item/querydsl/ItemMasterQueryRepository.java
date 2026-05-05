package com.upmudoum.trade.domain.item.querydsl;

import static com.upmudoum.trade.domain.item.entity.QItemMaster.itemMaster;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.upmudoum.trade.domain.analysis.dto.MarketGroupStrengthDto;
import com.upmudoum.trade.domain.analysis.dto.RankingItemDto;
import com.upmudoum.trade.domain.item.dto.ItemSearchResultDto;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ItemMasterQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ItemMasterQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<ItemSearchResultDto> searchByCodeOrName(String keyword, int limit) {
        return queryFactory
                .select(Projections.bean(
                        ItemSearchResultDto.class,
                        itemMaster.itemCode,
                        itemMaster.itemName,
                        itemMaster.marketCode,
                        itemMaster.masterType.stringValue().as("masterType")
                ))
                .from(itemMaster)
                .where(keywordContains(keyword))
                .orderBy(itemMaster.marketCode.asc(), itemMaster.itemCode.asc())
                .limit(Math.max(1, Math.min(limit, 50)))
                .fetch();
    }

    public List<RankingItemDto> rankFromMaster(String rankingType, TradeMasterType masterType, int limit) {
        NumberExpression<BigDecimal> metric = itemMetric(rankingType);
        return queryFactory
                .select(Projections.bean(
                        RankingItemDto.class,
                        itemMaster.itemCode,
                        itemMaster.itemName,
                        itemMaster.marketCode,
                        itemMaster.countryCode,
                        itemMaster.sectorName,
                        metric.as("metricValue")
                ))
                .from(itemMaster)
                .where(masterTypeEq(masterType))
                .orderBy(order(metric, rankingType))
                .limit(Math.max(1, Math.min(limit, 200)))
                .fetch();
    }

    public List<MarketGroupStrengthDto> sectorStrengths() {
        return groupStrength(unknownIfNull(itemMaster.sectorName));
    }

    public List<MarketGroupStrengthDto> themeStrengths() {
        return groupStrength(unknownIfNull(itemMaster.marketCode));
    }

    private List<MarketGroupStrengthDto> groupStrength(StringExpression groupExpression) {
        NumberExpression<BigDecimal> strengthScore = itemMaster.marketCap.coalesce(BigDecimal.ZERO).sum();
        return queryFactory
                .select(Projections.bean(
                        MarketGroupStrengthDto.class,
                        ExpressionUtils.as(groupExpression, "groupName"),
                        itemMaster.id.count().intValue().as("itemCount"),
                        strengthScore.as("strengthScore")
                ))
                .from(itemMaster)
                .groupBy(groupExpression)
                .orderBy(strengthScore.desc())
                .fetch();
    }

    private StringExpression unknownIfNull(StringExpression expression) {
        return Expressions.stringTemplate("coalesce({0}, 'UNKNOWN')", expression);
    }

    private BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        String normalized = keyword.trim();
        return itemMaster.itemCode.containsIgnoreCase(normalized)
                .or(itemMaster.itemName.containsIgnoreCase(normalized));
    }

    private BooleanExpression masterTypeEq(TradeMasterType masterType) {
        return masterType == null ? null : itemMaster.masterType.eq(masterType);
    }

    private NumberExpression<BigDecimal> itemMetric(String rankingType) {
        return switch (rankingType) {
            case "high52" -> itemMaster.high52WeekPrice.coalesce(BigDecimal.ZERO);
            case "low52" -> itemMaster.low52WeekPrice.coalesce(BigDecimal.ZERO);
            default -> itemMaster.marketCap.coalesce(BigDecimal.ZERO);
        };
    }

    private OrderSpecifier<BigDecimal> order(NumberExpression<BigDecimal> metric, String rankingType) {
        if ("low52".equals(rankingType) || "losers".equals(rankingType)) {
            return metric.asc();
        }
        return metric.desc();
    }
}
