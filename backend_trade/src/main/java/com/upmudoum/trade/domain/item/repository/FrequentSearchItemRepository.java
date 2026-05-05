package com.upmudoum.trade.domain.item.repository;

import com.upmudoum.trade.domain.item.entity.FrequentSearchItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FrequentSearchItemRepository extends JpaRepository<FrequentSearchItem, Long> {

    Optional<FrequentSearchItem> findByUserIdAndItemCode(String userId, String itemCode);

    List<FrequentSearchItem> findTop20ByUserIdOrderBySearchCountDescLastSearchedAtDesc(String userId);
}
