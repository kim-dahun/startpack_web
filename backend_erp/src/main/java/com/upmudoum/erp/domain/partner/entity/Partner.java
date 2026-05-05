package com.upmudoum.erp.domain.partner.entity;

import com.upmudoum.erp.domain.partner.vo.PartnerCode;
import com.upmudoum.erp.domain.partner.vo.PartnerStatus;
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
@Table(name = "erp_partners")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private PartnerCode code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 30)
    private String businessNumber;

    @Column(nullable = false, length = 20)
    private String partnerType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PartnerStatus status = PartnerStatus.ACTIVE;

    public Partner(String code, String name, String businessNumber, String partnerType) {
        this.code = PartnerCode.of(code);
        this.name = name;
        this.businessNumber = businessNumber;
        this.partnerType = partnerType;
    }

    public void update(String name, String businessNumber, String partnerType, PartnerStatus status) {
        this.name = name;
        this.businessNumber = businessNumber;
        this.partnerType = partnerType;
        this.status = status;
    }
}
