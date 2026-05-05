package com.upmudoum.erp.domain.equipment.entity;

import com.upmudoum.erp.domain.process.entity.ErpProcess;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_equipment_processes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_equipment_process", columnNames = {"equipment_id", "process_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EquipmentProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "process_id", nullable = false)
    private ErpProcess process;

    @Column(nullable = false)
    private boolean enabled = true;

    public EquipmentProcess(Equipment equipment, ErpProcess process) {
        this.equipment = equipment;
        this.process = process;
    }

    public void disable() {
        this.enabled = false;
    }
}
