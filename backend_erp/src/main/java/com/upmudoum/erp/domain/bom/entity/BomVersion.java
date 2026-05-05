package com.upmudoum.erp.domain.bom.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_bom_versions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BomVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bom_id", nullable = false)
    private Bom bom;

    @Column(nullable = false, length = 30)
    private String versionNo;

    @Column(nullable = false)
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    @Column(nullable = false)
    private boolean enabled = true;

    public BomVersion(Bom bom, String versionNo, LocalDate effectiveFrom, LocalDate effectiveTo) {
        this.bom = bom;
        this.versionNo = versionNo;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
    }
}
