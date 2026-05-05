package com.upmudoum.erp.domain.warehouse.entity;

import com.upmudoum.erp.domain.warehouse.vo.WarehouseCode;
import com.upmudoum.erp.domain.warehouse.vo.WarehouseStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_warehouses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private WarehouseCode code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WarehouseStatus status = WarehouseStatus.ACTIVE;

    public Warehouse(String code, String name, String location) {
        this.code = WarehouseCode.of(code);
        this.name = name;
        this.location = location;
    }

    public void update(String name, String location, WarehouseStatus status) {
        this.name = name;
        this.location = location;
        this.status = status;
    }
}
