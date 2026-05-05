package com.upmudoum.erp.domain.partner.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.upmudoum.erp.domain.partner.entity.Partner;
import com.upmudoum.erp.domain.partner.entity.QPartner;
import com.upmudoum.erp.domain.partner.vo.PartnerStatus;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class PartnerQueryRepository {

    private final JPAQueryFactory queryFactory;

    public PartnerQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<Partner> search(String partnerType, PartnerStatus status, String keyword) {
        QPartner partner = QPartner.partner;
        BooleanBuilder condition = new BooleanBuilder();
        if (partnerType != null && !partnerType.isBlank()) {
            condition.and(partner.partnerType.eq(partnerType));
        }
        if (status != null) {
            condition.and(partner.status.eq(status));
        }
        if (keyword != null && !keyword.isBlank()) {
            condition.and(partner.name.containsIgnoreCase(keyword)
                    .or(partner.code.value.containsIgnoreCase(keyword))
                    .or(partner.businessNumber.containsIgnoreCase(keyword)));
        }
        return queryFactory.selectFrom(partner)
                .where(condition)
                .orderBy(partner.code.value.asc())
                .fetch();
    }
}
