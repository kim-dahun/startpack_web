package com.upmudoum.trade.domain.watchlist.repository;

import com.upmudoum.trade.domain.watchlist.entity.WatchlistItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchlistRepository extends JpaRepository<WatchlistItem, Long> {

    List<WatchlistItem> findByUserIdOrderByCreatedAtDesc(String userId);

    List<WatchlistItem> findByUserIdAndGroupIdOrderByCreatedAtDesc(String userId, Long groupId);

    Optional<WatchlistItem> findByUserIdAndItemCode(String userId, String itemCode);
}
