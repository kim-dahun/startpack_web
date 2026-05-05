package com.upmudoum.erp.domain.sales.entity;

import com.upmudoum.erp.domain.accounting.vo.MoneyAmount;
import com.upmudoum.erp.domain.partner.entity.Partner;
import com.upmudoum.erp.domain.sales.vo.SalesShipmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_sales_shipments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SalesShipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @Column(nullable = false)
    private LocalDate salesDate;

    @Embedded
    private MoneyAmount totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SalesShipmentStatus status = SalesShipmentStatus.SHIPPED;

    public SalesShipment(Partner partner, LocalDate salesDate, BigDecimal totalAmount) {
        this.partner = partner;
        this.salesDate = salesDate;
        this.totalAmount = MoneyAmount.of(totalAmount);
    }
}
