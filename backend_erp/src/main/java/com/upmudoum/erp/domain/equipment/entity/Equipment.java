package com.upmudoum.erp.domain.equipment.entity;

import com.upmudoum.erp.domain.equipment.vo.EquipmentStatus;
import com.upmudoum.erp.domain.warehouse.entity.Warehouse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_equipments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 30)
    private String equipmentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Column(length = 200)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EquipmentStatus status = EquipmentStatus.AVAILABLE;

    @Column(nullable = false)
    private boolean enabled = true;

    public Equipment(String code, String name, String equipmentType, Warehouse warehouse, String location,
                     EquipmentStatus status) {
        this.code = code;
        this.name = name;
        this.equipmentType = equipmentType;
        this.warehouse = warehouse;
        this.location = location;
        this.status = status == null ? EquipmentStatus.AVAILABLE : status;
    }

    public void update(String name, String equipmentType, Warehouse warehouse, String location,
                       EquipmentStatus status, boolean enabled) {
        this.name = name;
        this.equipmentType = equipmentType;
        this.warehouse = warehouse;
        this.location = location;
        this.status = status;
        this.enabled = enabled;
    }
}
