package com.upmudoum.erp.domain.equipment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEquipmentProcess is a Querydsl query type for EquipmentProcess
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEquipmentProcess extends EntityPathBase<EquipmentProcess> {

    private static final long serialVersionUID = -731312952L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEquipmentProcess equipmentProcess = new QEquipmentProcess("equipmentProcess");

    public final BooleanPath enabled = createBoolean("enabled");

    public final QEquipment equipment;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.upmudoum.erp.domain.process.entity.QErpProcess process;

    public QEquipmentProcess(String variable) {
        this(EquipmentProcess.class, forVariable(variable), INITS);
    }

    public QEquipmentProcess(Path<? extends EquipmentProcess> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEquipmentProcess(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEquipmentProcess(PathMetadata metadata, PathInits inits) {
        this(EquipmentProcess.class, metadata, inits);
    }

    public QEquipmentProcess(Class<? extends EquipmentProcess> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.equipment = inits.isInitialized("equipment") ? new QEquipment(forProperty("equipment"), inits.get("equipment")) : null;
        this.process = inits.isInitialized("process") ? new com.upmudoum.erp.domain.process.entity.QErpProcess(forProperty("process")) : null;
    }

}

