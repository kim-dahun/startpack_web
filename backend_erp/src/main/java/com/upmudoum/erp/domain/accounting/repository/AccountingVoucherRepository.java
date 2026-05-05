package com.upmudoum.erp.domain.accounting.repository;

import com.upmudoum.erp.domain.accounting.entity.AccountingVoucher;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountingVoucherRepository extends JpaRepository<AccountingVoucher, Long> {

    boolean existsByVoucherNo(String voucherNo);

    boolean existsBySourceEventTypeAndSourceEventId(String sourceEventType, String sourceEventId);

    List<AccountingVoucher> findBySourceEventTypeAndSourceEventIdOrderByIdDesc(String sourceEventType, String sourceEventId);
}
