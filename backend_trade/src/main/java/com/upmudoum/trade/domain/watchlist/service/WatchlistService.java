package com.upmudoum.trade.domain.watchlist.service;

import com.upmudoum.trade.domain.watchlist.dto.CreateWatchlistItemRequest;
import com.upmudoum.trade.domain.watchlist.dto.UpdateWatchlistItemMetadataRequest;
import com.upmudoum.trade.domain.watchlist.dto.WatchlistItemDto;
import com.upmudoum.trade.domain.watchlist.entity.WatchlistItem;
import com.upmudoum.trade.domain.watchlist.querydsl.WatchlistQueryRepository;
import com.upmudoum.trade.domain.watchlist.repository.WatchlistGroupRepository;
import com.upmudoum.trade.domain.watchlist.repository.WatchlistRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class WatchlistService {

    private final WatchlistRepository repository;
    private final WatchlistGroupRepository groupRepository;
    private final WatchlistQueryRepository queryRepository;

    public WatchlistService(
            WatchlistRepository repository,
            WatchlistGroupRepository groupRepository,
            WatchlistQueryRepository queryRepository
    ) {
        this.repository = repository;
        this.groupRepository = groupRepository;
        this.queryRepository = queryRepository;
    }

    public List<WatchlistItemDto> findByUserId(String userId) {
        return queryRepository.findItems(userId, null).stream().map(this::toDto).toList();
    }

    public List<WatchlistItemDto> findByUserIdAndGroupId(String userId, Long groupId) {
        return queryRepository.findItems(userId, groupId).stream().map(this::toDto).toList();
    }

    public WatchlistItemDto add(CreateWatchlistItemRequest request) {
        repository.findByUserIdAndItemCode(request.getUserId(), request.getItemCode())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("watchlist item already exists");
                });
        validateGroup(request.getUserId(), request.getGroupId());

        WatchlistItem item = new WatchlistItem();
        item.setUserId(request.getUserId());
        item.setItemCode(request.getItemCode());
        item.setItemName(request.getItemName());
        item.setGroupId(request.getGroupId());
        item.setMemo(request.getMemo());
        item.setTags(toTagText(request.getTags()));
        item.setCreatedAt(Instant.now());
        return toDto(repository.save(item));
    }

    public WatchlistItemDto updateMetadata(Long id, UpdateWatchlistItemMetadataRequest request) {
        WatchlistItem item = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("watchlist item not found"));
        validateGroup(item.getUserId(), request.getGroupId());
        item.setGroupId(request.getGroupId());
        item.setMemo(request.getMemo());
        item.setTags(toTagText(request.getTags()));
        return toDto(repository.save(item));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private WatchlistItemDto toDto(WatchlistItem item) {
        WatchlistItemDto dto = new WatchlistItemDto();
        dto.setId(item.getId());
        dto.setUserId(item.getUserId());
        dto.setItemCode(item.getItemCode());
        dto.setItemName(item.getItemName());
        dto.setGroupId(item.getGroupId());
        dto.setMemo(item.getMemo());
        dto.setTags(toTags(item.getTags()));
        dto.setCreatedAt(item.getCreatedAt());
        return dto;
    }

    private void validateGroup(String userId, Long groupId) {
        if (groupId == null) {
            return;
        }
        groupRepository.findById(groupId)
                .filter(group -> group.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("watchlist group not found"));
    }

    private String toTagText(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return tags.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .map(tag -> tag.replace(",", ""))
                .distinct()
                .reduce((left, right) -> left + "," + right)
                .orElse(null);
    }

    private List<String> toTags(String tagText) {
        if (tagText == null || tagText.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tagText.split(","))
                .filter(tag -> !tag.isBlank())
                .toList();
    }
}
