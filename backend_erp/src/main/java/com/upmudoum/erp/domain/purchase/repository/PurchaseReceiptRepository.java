package com.upmudoum.erp.domain.purchase.repository;

import com.upmudoum.erp.domain.purchase.entity.PurchaseReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseReceiptRepository extends JpaRepository<PurchaseReceipt, Long> {
}
