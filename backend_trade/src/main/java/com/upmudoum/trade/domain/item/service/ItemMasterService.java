package com.upmudoum.trade.domain.item.service;

import com.upmudoum.trade.domain.item.dto.ItemDto;
import com.upmudoum.trade.domain.item.entity.ItemMaster;
import com.upmudoum.trade.domain.item.repository.ItemMasterRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemMasterService {

    private final ItemMasterRepository repository;

    public ItemMasterService(ItemMasterRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void saveAll(List<ItemDto> items) {
        items.forEach(this::save);
    }

    @Transactional
    public void save(ItemDto item) {
        ItemMaster entity = repository.findByItemCode(item.getItemCode()).orElseGet(ItemMaster::new);
        entity.setItemCode(item.getItemCode());
        entity.setItemName(item.getItemName());
        entity.setMarketCode(item.getMarketCode() == null || item.getMarketCode().isBlank() ? "UNKNOWN" : item.getMarketCode());
        entity.setSyncedAt(Instant.now());
        repository.save(entity);
    }
}
