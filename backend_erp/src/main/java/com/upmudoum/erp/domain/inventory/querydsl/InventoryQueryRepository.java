package com.upmudoum.erp.domain.inventory.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.upmudoum.erp.domain.inventory.entity.InventoryBalance;
import com.upmudoum.erp.domain.inventory.entity.InventoryLotBalance;
import com.upmudoum.erp.domain.inventory.entity.QInventoryBalance;
import com.upmudoum.erp.domain.inventory.entity.QInventoryLotBalance;
import com.upmudoum.erp.domain.item.vo.ItemType;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryQueryRepository {

    private final JPAQueryFactory queryFactory;

    public InventoryQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<InventoryBalance> searchBalances(Long warehouseId, ItemType itemType, Boolean positiveOnly, String keyword) {
        QInventoryBalance balance = QInventoryBalance.inventoryBalance;
        BooleanBuilder condition = new BooleanBuilder();
        if (warehouseId != null) {
            condition.and(balance.warehouse.id.eq(warehouseId));
        }
        if (itemType != null) {
            condition.and(balance.item.itemType.eq(itemType));
        }
        if (Boolean.TRUE.equals(positiveOnly)) {
            condition.and(balance.quantity.value.gt(BigDecimal.ZERO));
        }
        if (keyword != null && !keyword.isBlank()) {
            condition.and(balance.item.name.containsIgnoreCase(keyword)
                    .or(balance.item.code.value.containsIgnoreCase(keyword)));
        }
        return queryFactory.selectFrom(balance)
                .where(condition)
                .orderBy(balance.warehouse.code.value.asc(), balance.item.code.value.asc())
                .fetch();
    }

    public List<InventoryLotBalance> searchLotBalances(Long itemId, Long warehouseId, String lotNo,
                                                       Boolean positiveOnly) {
        QInventoryLotBalance balance = QInventoryLotBalance.inventoryLotBalance;
        BooleanBuilder condition = new BooleanBuilder();
        if (itemId != null) {
            condition.and(balance.item.id.eq(itemId));
        }
        if (warehouseId != null) {
            condition.and(balance.warehouse.id.eq(warehouseId));
        }
        if (lotNo != null && !lotNo.isBlank()) {
            condition.and(balance.lot.lotNo.containsIgnoreCase(lotNo));
        }
        if (Boolean.TRUE.equals(positiveOnly)) {
            condition.and(balance.quantity.value.gt(BigDecimal.ZERO));
        }
        return queryFactory.selectFrom(balance)
                .where(condition)
                .orderBy(balance.item.code.value.asc(), balance.warehouse.code.value.asc(), balance.firstReceivedAt.asc())
                .fetch();
    }
}
