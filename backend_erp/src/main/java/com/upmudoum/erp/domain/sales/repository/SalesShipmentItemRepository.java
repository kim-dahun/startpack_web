package com.upmudoum.erp.domain.sales.repository;

import com.upmudoum.erp.domain.sales.entity.SalesShipmentItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesShipmentItemRepository extends JpaRepository<SalesShipmentItem, Long> {

    List<SalesShipmentItem> findBySalesShipmentId(Long salesShipmentId);

    List<SalesShipmentItem> findByItemIdOrderByIdDesc(Long itemId);
}
