package com.upmudoum.trade.domain.event.querydsl;

import static com.upmudoum.trade.domain.event.entity.QTradeEvent.tradeEvent;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.upmudoum.trade.domain.event.dto.TradeEventDto;
import com.upmudoum.trade.domain.event.vo.TradeEventType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class TradeEventQueryRepository {

    private final JPAQueryFactory queryFactory;

    public TradeEventQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<TradeEventDto> find(TradeEventType eventType, LocalDate from, LocalDate to, int limit) {
        return queryFactory
                .select(Projections.bean(
                        TradeEventDto.class,
                        tradeEvent.id,
                        tradeEvent.eventType,
                        tradeEvent.itemCode,
                        tradeEvent.title,
                        tradeEvent.eventDate,
                        tradeEvent.description
                ))
                .from(tradeEvent)
                .where(eventTypeEq(eventType), fromGoe(from), toLoe(to))
                .orderBy(tradeEvent.eventDate.desc(), tradeEvent.id.desc())
                .limit(Math.max(1, Math.min(limit, 500)))
                .fetch();
    }

    private BooleanExpression eventTypeEq(TradeEventType eventType) {
        return eventType == null ? null : tradeEvent.eventType.eq(eventType);
    }

    private BooleanExpression fromGoe(LocalDate from) {
        return from == null ? null : tradeEvent.eventDate.goe(from);
    }

    private BooleanExpression toLoe(LocalDate to) {
        return to == null ? null : tradeEvent.eventDate.loe(to);
    }
}
