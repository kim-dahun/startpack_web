package com.upmudoum.trade.domain.account.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRegisteredAccount is a Querydsl query type for RegisteredAccount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRegisteredAccount extends EntityPathBase<RegisteredAccount> {

    private static final long serialVersionUID = -951895706L;

    public static final QRegisteredAccount registeredAccount = new QRegisteredAccount("registeredAccount");

    public final StringPath accountName = createString("accountName");

    public final StringPath accountNo = createString("accountNo");

    public final BooleanPath active = createBoolean("active");

    public final StringPath aliasName = createString("aliasName");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final StringPath productCode = createString("productCode");

    public final DateTimePath<java.time.Instant> updatedAt = createDateTime("updatedAt", java.time.Instant.class);

    public QRegisteredAccount(String variable) {
        super(RegisteredAccount.class, forVariable(variable));
    }

    public QRegisteredAccount(Path<? extends RegisteredAccount> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRegisteredAccount(PathMetadata metadata) {
        super(RegisteredAccount.class, metadata);
    }

}

