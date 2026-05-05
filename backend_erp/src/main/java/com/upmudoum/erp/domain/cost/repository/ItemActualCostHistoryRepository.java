package com.upmudoum.erp.domain.cost.repository;

import com.upmudoum.erp.domain.cost.entity.ItemActualCostHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemActualCostHistoryRepository extends JpaRepository<ItemActualCostHistory, Long> {

    List<ItemActualCostHistory> findByItemId(Long itemId);

    List<ItemActualCostHistory> findByReferenceTypeAndReferenceId(com.upmudoum.erp.domain.cost.vo.ActualCostReferenceType referenceType,
                                                                  Long referenceId);
}
