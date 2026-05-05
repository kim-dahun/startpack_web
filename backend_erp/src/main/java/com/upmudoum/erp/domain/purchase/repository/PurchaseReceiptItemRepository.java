package com.upmudoum.erp.domain.purchase.repository;

import com.upmudoum.erp.domain.purchase.entity.PurchaseReceiptItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseReceiptItemRepository extends JpaRepository<PurchaseReceiptItem, Long> {

    List<PurchaseReceiptItem> findByPurchaseReceiptId(Long purchaseReceiptId);

    List<PurchaseReceiptItem> findByItemIdOrderByIdDesc(Long itemId);
}
