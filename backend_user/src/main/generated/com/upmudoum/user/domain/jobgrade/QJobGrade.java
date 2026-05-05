package com.upmudoum.user.domain.jobgrade;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QJobGrade is a Querydsl query type for JobGrade
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJobGrade extends EntityPathBase<JobGrade> {

    private static final long serialVersionUID = 1557209868L;

    public static final QJobGrade jobGrade = new QJobGrade("jobGrade");

    public final com.upmudoum.user.domain.common.QBaseAuditEntity _super = new com.upmudoum.user.domain.common.QBaseAuditEntity(this);

    public final StringPath comCd = createString("comCd");

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath jobGradeId = createString("jobGradeId");

    public final StringPath jobGradeName = createString("jobGradeName");

    public final EnumPath<JobGradeType> jobGradeType = createEnum("jobGradeType", JobGradeType.class);

    public final NumberPath<Integer> sortSeq = createNumber("sortSeq", Integer.class);

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    public QJobGrade(String variable) {
        super(JobGrade.class, forVariable(variable));
    }

    public QJobGrade(Path<? extends JobGrade> path) {
        super(path.getType(), path.getMetadata());
    }

    public QJobGrade(PathMetadata metadata) {
        super(JobGrade.class, metadata);
    }

}

