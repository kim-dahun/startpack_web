package com.upmudoum.erp.domain.process.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QErpProcess is a Querydsl query type for ErpProcess
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QErpProcess extends EntityPathBase<ErpProcess> {

    private static final long serialVersionUID = -668825036L;

    public static final QErpProcess erpProcess = new QErpProcess("erpProcess");

    public final StringPath code = createString("code");

    public final StringPath description = createString("description");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath processType = createString("processType");

    public QErpProcess(String variable) {
        super(ErpProcess.class, forVariable(variable));
    }

    public QErpProcess(Path<? extends ErpProcess> path) {
        super(path.getType(), path.getMetadata());
    }

    public QErpProcess(PathMetadata metadata) {
        super(ErpProcess.class, metadata);
    }

}

