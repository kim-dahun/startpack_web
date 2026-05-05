package com.upmudoum.trade.domain.item.service;

import com.upmudoum.trade.domain.item.dto.FrequentSearchItemDto;
import com.upmudoum.trade.domain.item.dto.RecordFrequentSearchRequest;
import com.upmudoum.trade.domain.item.entity.FrequentSearchItem;
import com.upmudoum.trade.domain.item.repository.FrequentSearchItemRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FrequentSearchItemService {

    private final FrequentSearchItemRepository repository;

    public FrequentSearchItemService(FrequentSearchItemRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<FrequentSearchItemDto> findByUserId(String userId) {
        return repository.findTop20ByUserIdOrderBySearchCountDescLastSearchedAtDesc(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public FrequentSearchItemDto record(RecordFrequentSearchRequest request) {
        FrequentSearchItem item = repository.findByUserIdAndItemCode(request.getUserId(), request.getItemCode())
                .orElseGet(FrequentSearchItem::new);
        item.setUserId(request.getUserId());
        item.setItemCode(request.getItemCode());
        item.setItemName(request.getItemName());
        item.setMarketCode(request.getMarketCode());
        item.setSearchCount(item.getSearchCount() + 1);
        item.setLastSearchedAt(Instant.now());
        return toDto(repository.save(item));
    }

    private FrequentSearchItemDto toDto(FrequentSearchItem item) {
        FrequentSearchItemDto dto = new FrequentSearchItemDto();
        dto.setId(item.getId());
        dto.setUserId(item.getUserId());
        dto.setItemCode(item.getItemCode());
        dto.setItemName(item.getItemName());
        dto.setMarketCode(item.getMarketCode());
        dto.setSearchCount(item.getSearchCount());
        dto.setLastSearchedAt(item.getLastSearchedAt());
        return dto;
    }
}
