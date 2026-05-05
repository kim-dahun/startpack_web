package com.upmudoum.trade.domain.watchlist.service;

import com.upmudoum.trade.domain.watchlist.dto.CreateWatchlistGroupRequest;
import com.upmudoum.trade.domain.watchlist.dto.WatchlistGroupDto;
import com.upmudoum.trade.domain.watchlist.entity.WatchlistGroup;
import com.upmudoum.trade.domain.watchlist.repository.WatchlistGroupRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WatchlistGroupService {

    private final WatchlistGroupRepository repository;

    public WatchlistGroupService(WatchlistGroupRepository repository) {
        this.repository = repository;
    }

    public List<WatchlistGroupDto> findByUserId(String userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDto).toList();
    }

    public WatchlistGroupDto add(CreateWatchlistGroupRequest request) {
        repository.findByUserIdAndGroupName(request.getUserId(), request.getGroupName())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("watchlist group already exists");
                });
        WatchlistGroup group = new WatchlistGroup();
        group.setUserId(request.getUserId());
        group.setGroupName(request.getGroupName());
        group.setCreatedAt(Instant.now());
        return toDto(repository.save(group));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private WatchlistGroupDto toDto(WatchlistGroup group) {
        WatchlistGroupDto dto = new WatchlistGroupDto();
        dto.setId(group.getId());
        dto.setUserId(group.getUserId());
        dto.setGroupName(group.getGroupName());
        dto.setCreatedAt(group.getCreatedAt());
        return dto;
    }
}
