package com.upmudoum.erp.domain.print.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPrintBarcode is a Querydsl query type for PrintBarcode
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPrintBarcode extends EntityPathBase<PrintBarcode> {

    private static final long serialVersionUID = 1191045625L;

    public static final QPrintBarcode printBarcode = new QPrintBarcode("printBarcode");

    public final StringPath barcodeValue = createString("barcodeValue");

    public final StringPath documentKey = createString("documentKey");

    public final EnumPath<com.upmudoum.erp.domain.print.vo.PrintDocumentType> documentType = createEnum("documentType", com.upmudoum.erp.domain.print.vo.PrintDocumentType.class);

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QPrintBarcode(String variable) {
        super(PrintBarcode.class, forVariable(variable));
    }

    public QPrintBarcode(Path<? extends PrintBarcode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPrintBarcode(PathMetadata metadata) {
        super(PrintBarcode.class, metadata);
    }

}

