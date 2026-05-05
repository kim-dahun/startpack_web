package com.upmudoum.erp.domain.inventory.repository;

import com.upmudoum.erp.domain.inventory.entity.InventoryTransfer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryTransferRepository extends JpaRepository<InventoryTransfer, Long> {

    boolean existsByTransferNo(String transferNo);

    List<InventoryTransfer> findByItemIdOrderByTransferredAtDescIdDesc(Long itemId);
}
