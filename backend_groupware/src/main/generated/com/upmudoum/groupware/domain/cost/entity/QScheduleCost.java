package com.upmudoum.groupware.domain.cost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QScheduleCost is a Querydsl query type for ScheduleCost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QScheduleCost extends EntityPathBase<ScheduleCost> {

    private static final long serialVersionUID = 249904939L;

    public static final QScheduleCost scheduleCost = new QScheduleCost("scheduleCost");

    public final ComparablePath<java.util.UUID> accountId = createComparable("accountId", java.util.UUID.class);

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    public final StringPath comCd = createString("comCd");

    public final DatePath<java.time.LocalDate> costDate = createDate("costDate", java.time.LocalDate.class);

    public final ComparablePath<java.util.UUID> costItemId = createComparable("costItemId", java.util.UUID.class);

    public final BooleanPath deletedYn = createBoolean("deletedYn");

    public final StringPath description = createString("description");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath projectCode = createString("projectCode");

    public final ComparablePath<java.util.UUID> projectId = createComparable("projectId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> scheduleId = createComparable("scheduleId", java.util.UUID.class);

    public QScheduleCost(String variable) {
        super(ScheduleCost.class, forVariable(variable));
    }

    public QScheduleCost(Path<? extends ScheduleCost> path) {
        super(path.getType(), path.getMetadata());
    }

    public QScheduleCost(PathMetadata metadata) {
        super(ScheduleCost.class, metadata);
    }

}

