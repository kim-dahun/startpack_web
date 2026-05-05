package com.upmudoum.erp.domain.partner.vo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPartnerCode is a Querydsl query type for PartnerCode
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QPartnerCode extends BeanPath<PartnerCode> {

    private static final long serialVersionUID = -1314831094L;

    public static final QPartnerCode partnerCode = new QPartnerCode("partnerCode");

    public final StringPath value = createString("value");

    public QPartnerCode(String variable) {
        super(PartnerCode.class, forVariable(variable));
    }

    public QPartnerCode(Path<? extends PartnerCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPartnerCode(PathMetadata metadata) {
        super(PartnerCode.class, metadata);
    }

}

