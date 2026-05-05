package com.upmudoum.erp.domain.item.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.item.entity.QItem;
import com.upmudoum.erp.domain.item.vo.ItemType;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ItemQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ItemQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<Item> search(ItemType itemType, Boolean active, String keyword) {
        QItem item = QItem.item;
        BooleanBuilder condition = new BooleanBuilder();
        if (itemType != null) {
            condition.and(item.itemType.eq(itemType));
        }
        if (active != null) {
            condition.and(item.active.eq(active));
        }
        if (keyword != null && !keyword.isBlank()) {
            condition.and(item.name.containsIgnoreCase(keyword)
                    .or(item.code.value.containsIgnoreCase(keyword)));
        }
        return queryFactory.selectFrom(item)
                .where(condition)
                .orderBy(item.code.value.asc())
                .fetch();
    }
}
