package com.upmudoum.erp.domain.accounting.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAccountingVoucher is a Querydsl query type for AccountingVoucher
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccountingVoucher extends EntityPathBase<AccountingVoucher> {

    private static final long serialVersionUID = -682144637L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAccountingVoucher accountingVoucher = new QAccountingVoucher("accountingVoucher");

    public final com.upmudoum.erp.domain.accounting.vo.QMoneyAmount amount;

    public final StringPath currencyCode = createString("currencyCode");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath sourceEventId = createString("sourceEventId");

    public final StringPath sourceEventType = createString("sourceEventType");

    public final EnumPath<com.upmudoum.erp.domain.accounting.vo.VoucherStatus> status = createEnum("status", com.upmudoum.erp.domain.accounting.vo.VoucherStatus.class);

    public final DatePath<java.time.LocalDate> voucherDate = createDate("voucherDate", java.time.LocalDate.class);

    public final StringPath voucherNo = createString("voucherNo");

    public QAccountingVoucher(String variable) {
        this(AccountingVoucher.class, forVariable(variable), INITS);
    }

    public QAccountingVoucher(Path<? extends AccountingVoucher> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAccountingVoucher(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAccountingVoucher(PathMetadata metadata, PathInits inits) {
        this(AccountingVoucher.class, metadata, inits);
    }

    public QAccountingVoucher(Class<? extends AccountingVoucher> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.amount = inits.isInitialized("amount") ? new com.upmudoum.erp.domain.accounting.vo.QMoneyAmount(forProperty("amount")) : null;
    }

}

