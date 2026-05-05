package com.upmudoum.trade.domain.marketdata.querydsl;

import static com.upmudoum.trade.domain.marketdata.entity.QRealtimeReconnectHistory.realtimeReconnectHistory;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.upmudoum.trade.domain.marketdata.dto.RealtimeReconnectHistoryDto;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class RealtimeReconnectHistoryQueryRepository {

    private final JPAQueryFactory queryFactory;

    public RealtimeReconnectHistoryQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<RealtimeReconnectHistoryDto> findRecent(Boolean success, Instant from, Instant to, int limit) {
        return queryFactory
                .select(Projections.bean(
                        RealtimeReconnectHistoryDto.class,
                        realtimeReconnectHistory.id,
                        realtimeReconnectHistory.attemptedAt,
                        realtimeReconnectHistory.success,
                        realtimeReconnectHistory.subscriptionCount,
                        realtimeReconnectHistory.failureReason
                ))
                .from(realtimeReconnectHistory)
                .where(successEq(success), fromGoe(from), toLoe(to))
                .orderBy(realtimeReconnectHistory.attemptedAt.desc(), realtimeReconnectHistory.id.desc())
                .limit(Math.max(1, Math.min(limit, 200)))
                .fetch();
    }

    private BooleanExpression successEq(Boolean success) {
        return success == null ? null : realtimeReconnectHistory.success.eq(success);
    }

    private BooleanExpression fromGoe(Instant from) {
        return from == null ? null : realtimeReconnectHistory.attemptedAt.goe(from);
    }

    private BooleanExpression toLoe(Instant to) {
        return to == null ? null : realtimeReconnectHistory.attemptedAt.loe(to);
    }
}
