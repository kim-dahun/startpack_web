package com.upmudoum.erp.domain.route.entity;

import com.upmudoum.erp.domain.equipment.entity.Equipment;
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
@Table(name = "erp_route_steps", uniqueConstraints = {
        @UniqueConstraint(name = "uk_route_step_sequence", columnNames = {"route_id", "sequence_no"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RouteStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "sequence_no", nullable = false)
    private Integer sequenceNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "process_id", nullable = false)
    private ErpProcess process;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_equipment_id")
    private Equipment defaultEquipment;

    private Integer standardLeadTimeMinutes;

    @Column(length = 200)
    private String description;

    @Column(nullable = false)
    private boolean enabled = true;

    public RouteStep(Route route, Integer sequenceNo, ErpProcess process, Equipment defaultEquipment,
                     Integer standardLeadTimeMinutes, String description) {
        this.route = route;
        this.sequenceNo = sequenceNo;
        this.process = process;
        this.defaultEquipment = defaultEquipment;
        this.standardLeadTimeMinutes = standardLeadTimeMinutes;
        this.description = description;
    }

    public void update(Integer sequenceNo, ErpProcess process, Equipment defaultEquipment,
                       Integer standardLeadTimeMinutes, String description, boolean enabled) {
        this.sequenceNo = sequenceNo;
        this.process = process;
        this.defaultEquipment = defaultEquipment;
        this.standardLeadTimeMinutes = standardLeadTimeMinutes;
        this.description = description;
        this.enabled = enabled;
    }

    public void disable() {
        this.enabled = false;
    }
}
