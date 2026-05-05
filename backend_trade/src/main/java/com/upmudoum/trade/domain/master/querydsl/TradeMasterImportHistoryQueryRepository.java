package com.upmudoum.trade.domain.master.querydsl;

import static com.upmudoum.trade.domain.master.entity.QTradeMasterImportHistory.tradeMasterImportHistory;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.upmudoum.trade.domain.master.dto.TradeMasterImportHistoryDto;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class TradeMasterImportHistoryQueryRepository {

    private final JPAQueryFactory queryFactory;

    public TradeMasterImportHistoryQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<TradeMasterImportHistoryDto> findHistories(TradeMasterType masterType, int limit) {
        return queryFactory
                .select(Projections.bean(
                        TradeMasterImportHistoryDto.class,
                        tradeMasterImportHistory.id,
                        tradeMasterImportHistory.masterType,
                        tradeMasterImportHistory.sourceFileName,
                        tradeMasterImportHistory.sourceVersion,
                        tradeMasterImportHistory.importStatus,
                        tradeMasterImportHistory.importedCount,
                        tradeMasterImportHistory.startedAt,
                        tradeMasterImportHistory.finishedAt,
                        tradeMasterImportHistory.success,
                        tradeMasterImportHistory.failureReason
                ))
                .from(tradeMasterImportHistory)
                .where(masterTypeEq(masterType))
                .orderBy(tradeMasterImportHistory.startedAt.desc(), tradeMasterImportHistory.id.desc())
                .limit(Math.max(1, Math.min(limit, 200)))
                .fetch();
    }

    private BooleanExpression masterTypeEq(TradeMasterType masterType) {
        return masterType == null ? null : tradeMasterImportHistory.masterType.eq(masterType);
    }
}
