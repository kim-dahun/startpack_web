package com.upmudoum.groupware.domain.cost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCostAccount is a Querydsl query type for CostAccount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCostAccount extends EntityPathBase<CostAccount> {

    private static final long serialVersionUID = 105526425L;

    public static final QCostAccount costAccount = new QCostAccount("costAccount");

    public final StringPath accountName = createString("accountName");

    public final StringPath comCd = createString("comCd");

    public final BooleanPath deletedYn = createBoolean("deletedYn");

    public final BooleanPath enabled = createBoolean("enabled");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public QCostAccount(String variable) {
        super(CostAccount.class, forVariable(variable));
    }

    public QCostAccount(Path<? extends CostAccount> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCostAccount(PathMetadata metadata) {
        super(CostAccount.class, metadata);
    }

}

