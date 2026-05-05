package com.upmudoum.trade.domain.watchlist.repository;

import com.upmudoum.trade.domain.watchlist.entity.WatchlistGroup;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchlistGroupRepository extends JpaRepository<WatchlistGroup, Long> {

    List<WatchlistGroup> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<WatchlistGroup> findByUserIdAndGroupName(String userId, String groupName);
}
