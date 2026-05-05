package com.upmudoum.erp.domain.production.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.upmudoum.erp.domain.production.entity.ProductionOrder;
import com.upmudoum.erp.domain.production.entity.ProductionResult;
import com.upmudoum.erp.domain.production.entity.QProductionOrder;
import com.upmudoum.erp.domain.production.entity.QProductionResult;
import com.upmudoum.erp.domain.production.vo.ProductionOrderStatus;
import com.upmudoum.erp.domain.production.vo.ProductionResultStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ProductionQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ProductionQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<ProductionOrder> searchOrders(Long itemId, Long routeId, Long processId, Long equipmentId,
                                              ProductionOrderStatus status, LocalDate dueFrom, LocalDate dueTo,
                                              String keyword) {
        QProductionOrder order = QProductionOrder.productionOrder;
        BooleanBuilder condition = new BooleanBuilder();
        if (itemId != null) {
            condition.and(order.item.id.eq(itemId));
        }
        if (routeId != null) {
            condition.and(order.route.id.eq(routeId));
        }
        if (processId != null) {
            condition.and(order.plannedProcess.id.eq(processId));
        }
        if (equipmentId != null) {
            condition.and(order.plannedEquipment.id.eq(equipmentId));
        }
        if (status != null) {
            condition.and(order.status.eq(status));
        }
        if (dueFrom != null) {
            condition.and(order.dueDate.goe(dueFrom));
        }
        if (dueTo != null) {
            condition.and(order.dueDate.loe(dueTo));
        }
        if (keyword != null && !keyword.isBlank()) {
            condition.and(order.orderNo.containsIgnoreCase(keyword)
                    .or(order.item.code.value.containsIgnoreCase(keyword))
                    .or(order.item.name.containsIgnoreCase(keyword)));
        }
        return queryFactory.selectFrom(order)
                .where(condition)
                .orderBy(order.dueDate.desc(), order.id.desc())
                .fetch();
    }

    public List<ProductionResult> searchResults(Long productionOrderId, Long itemId, Long routeId, Long processId,
                                                Long equipmentId, ProductionResultStatus status,
                                                LocalDateTime completedFrom, LocalDateTime completedTo,
                                                String keyword) {
        QProductionResult result = QProductionResult.productionResult;
        BooleanBuilder condition = new BooleanBuilder();
        if (productionOrderId != null) {
            condition.and(result.productionOrder.id.eq(productionOrderId));
        }
        if (itemId != null) {
            condition.and(result.productionOrder.item.id.eq(itemId));
        }
        if (routeId != null) {
            condition.and(result.route.id.eq(routeId));
        }
        if (processId != null) {
            condition.and(result.process.id.eq(processId));
        }
        if (equipmentId != null) {
            condition.and(result.equipment.id.eq(equipmentId));
        }
        if (status != null) {
            condition.and(result.status.eq(status));
        }
        if (completedFrom != null) {
            condition.and(result.completedAt.goe(completedFrom));
        }
        if (completedTo != null) {
            condition.and(result.completedAt.loe(completedTo));
        }
        if (keyword != null && !keyword.isBlank()) {
            condition.and(result.productionOrder.orderNo.containsIgnoreCase(keyword)
                    .or(result.productionOrder.item.code.value.containsIgnoreCase(keyword))
                    .or(result.productionOrder.item.name.containsIgnoreCase(keyword)));
        }
        return queryFactory.selectFrom(result)
                .where(condition)
                .orderBy(result.completedAt.desc(), result.id.desc())
                .fetch();
    }
}
