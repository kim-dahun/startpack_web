package com.upmudoum.trade.domain.chart.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QChartDrawing is a Querydsl query type for ChartDrawing
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChartDrawing extends EntityPathBase<ChartDrawing> {

    private static final long serialVersionUID = -1420117162L;

    public static final QChartDrawing chartDrawing = new QChartDrawing("chartDrawing");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final EnumPath<com.upmudoum.trade.domain.chart.vo.ChartDrawingType> drawingType = createEnum("drawingType", com.upmudoum.trade.domain.chart.vo.ChartDrawingType.class);

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final NumberPath<java.math.BigDecimal> endPrice = createNumber("endPrice", java.math.BigDecimal.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath itemCode = createString("itemCode");

    public final StringPath memo = createString("memo");

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final NumberPath<java.math.BigDecimal> startPrice = createNumber("startPrice", java.math.BigDecimal.class);

    public final DateTimePath<java.time.Instant> updatedAt = createDateTime("updatedAt", java.time.Instant.class);

    public final StringPath userId = createString("userId");

    public QChartDrawing(String variable) {
        super(ChartDrawing.class, forVariable(variable));
    }

    public QChartDrawing(Path<? extends ChartDrawing> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChartDrawing(PathMetadata metadata) {
        super(ChartDrawing.class, metadata);
    }

}

