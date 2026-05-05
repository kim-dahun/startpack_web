package com.upmudoum.user.domain.menu;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMenuPermission is a Querydsl query type for MenuPermission
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMenuPermission extends EntityPathBase<MenuPermission> {

    private static final long serialVersionUID = 2061090107L;

    public static final QMenuPermission menuPermission = new QMenuPermission("menuPermission");

    public final com.upmudoum.user.domain.common.QBaseAuditEntity _super = new com.upmudoum.user.domain.common.QBaseAuditEntity(this);

    public final StringPath comCd = createString("comCd");

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final BooleanPath deletable = createBoolean("deletable");

    public final BooleanPath excelDownable = createBoolean("excelDownable");

    public final StringPath groupId = createString("groupId");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath menuId = createString("menuId");

    public final BooleanPath readable = createBoolean("readable");

    public final StringPath serviceId = createString("serviceId");

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    public final BooleanPath writable = createBoolean("writable");

    public QMenuPermission(String variable) {
        super(MenuPermission.class, forVariable(variable));
    }

    public QMenuPermission(Path<? extends MenuPermission> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMenuPermission(PathMetadata metadata) {
        super(MenuPermission.class, metadata);
    }

}

