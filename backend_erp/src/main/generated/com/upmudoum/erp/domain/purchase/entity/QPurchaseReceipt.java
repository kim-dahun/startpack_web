package com.upmudoum.erp.domain.purchase.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPurchaseReceipt is a Querydsl query type for PurchaseReceipt
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPurchaseReceipt extends EntityPathBase<PurchaseReceipt> {

    private static final long serialVersionUID = 1927645621L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPurchaseReceipt purchaseReceipt = new QPurchaseReceipt("purchaseReceipt");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.partner.entity.QPartner partner;

    public final DatePath<java.time.LocalDate> purchaseDate = createDate("purchaseDate", java.time.LocalDate.class);

    public final EnumPath<com.upmudoum.erp.domain.purchase.vo.PurchaseReceiptStatus> status = createEnum("status", com.upmudoum.erp.domain.purchase.vo.PurchaseReceiptStatus.class);

    public final com.upmudoum.erp.domain.accounting.vo.QMoneyAmount totalAmount;

    public QPurchaseReceipt(String variable) {
        this(PurchaseReceipt.class, forVariable(variable), INITS);
    }

    public QPurchaseReceipt(Path<? extends PurchaseReceipt> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPurchaseReceipt(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPurchaseReceipt(PathMetadata metadata, PathInits inits) {
        this(PurchaseReceipt.class, metadata, inits);
    }

    public QPurchaseReceipt(Class<? extends PurchaseReceipt> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.partner = inits.isInitialized("partner") ? new com.upmudoum.erp.domain.partner.entity.QPartner(forProperty("partner"), inits.get("partner")) : null;
        this.totalAmount = inits.isInitialized("totalAmount") ? new com.upmudoum.erp.domain.accounting.vo.QMoneyAmount(forProperty("totalAmount")) : null;
    }

}

