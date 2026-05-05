package com.upmudoum.erp.domain.accounting.entity;

import com.upmudoum.erp.domain.accounting.vo.MoneyAmount;
import com.upmudoum.erp.domain.accounting.vo.VoucherStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_accounting_vouchers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountingVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String voucherNo;

    @Column(nullable = false)
    private LocalDate voucherDate;

    @Embedded
    private MoneyAmount amount;

    @Column(nullable = false, length = 3)
    private String currencyCode;

    @Column(length = 100)
    private String sourceEventType;

    @Column(length = 100)
    private String sourceEventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VoucherStatus status = VoucherStatus.DRAFT;

    public AccountingVoucher(String voucherNo, LocalDate voucherDate, BigDecimal amount, String currencyCode,
                             String sourceEventType, String sourceEventId) {
        this.voucherNo = voucherNo;
        this.voucherDate = voucherDate;
        this.amount = MoneyAmount.of(amount);
        this.currencyCode = currencyCode;
        this.sourceEventType = sourceEventType;
        this.sourceEventId = sourceEventId;
    }

    public void post() {
        this.status = VoucherStatus.POSTED;
    }
}
