package com.upmudoum.trade.domain.watchlist.querydsl;

import static com.upmudoum.trade.domain.watchlist.entity.QWatchlistItem.watchlistItem;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.upmudoum.trade.domain.watchlist.entity.WatchlistItem;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class WatchlistQueryRepository {

    private final JPAQueryFactory queryFactory;

    public WatchlistQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<WatchlistItem> findItems(String userId, Long groupId) {
        return queryFactory
                .selectFrom(watchlistItem)
                .where(userIdEq(userId), groupIdEq(groupId))
                .orderBy(watchlistItem.createdAt.desc(), watchlistItem.id.desc())
                .fetch();
    }

    private BooleanExpression userIdEq(String userId) {
        return watchlistItem.userId.eq(userId);
    }

    private BooleanExpression groupIdEq(Long groupId) {
        return groupId == null ? null : watchlistItem.groupId.eq(groupId);
    }
}
