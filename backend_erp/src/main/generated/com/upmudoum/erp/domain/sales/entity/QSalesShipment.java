package com.upmudoum.erp.domain.sales.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSalesShipment is a Querydsl query type for SalesShipment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSalesShipment extends EntityPathBase<SalesShipment> {

    private static final long serialVersionUID = -821898847L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSalesShipment salesShipment = new QSalesShipment("salesShipment");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.partner.entity.QPartner partner;

    public final DatePath<java.time.LocalDate> salesDate = createDate("salesDate", java.time.LocalDate.class);

    public final EnumPath<com.upmudoum.erp.domain.sales.vo.SalesShipmentStatus> status = createEnum("status", com.upmudoum.erp.domain.sales.vo.SalesShipmentStatus.class);

    public final com.upmudoum.erp.domain.accounting.vo.QMoneyAmount totalAmount;

    public QSalesShipment(String variable) {
        this(SalesShipment.class, forVariable(variable), INITS);
    }

    public QSalesShipment(Path<? extends SalesShipment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSalesShipment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSalesShipment(PathMetadata metadata, PathInits inits) {
        this(SalesShipment.class, metadata, inits);
    }

    public QSalesShipment(Class<? extends SalesShipment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.partner = inits.isInitialized("partner") ? new com.upmudoum.erp.domain.partner.entity.QPartner(forProperty("partner"), inits.get("partner")) : null;
        this.totalAmount = inits.isInitialized("totalAmount") ? new com.upmudoum.erp.domain.accounting.vo.QMoneyAmount(forProperty("totalAmount")) : null;
    }

}

