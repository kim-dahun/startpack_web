package com.upmudoum.erp.domain.item.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.item.dto.ItemRequest;
import com.upmudoum.erp.domain.item.dto.ItemResponse;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.item.querydsl.ItemQueryRepository;
import com.upmudoum.erp.domain.item.repository.ItemRepository;
import com.upmudoum.erp.domain.item.vo.ItemType;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemQueryRepository itemQueryRepository;

    public ItemService(ItemRepository itemRepository, ItemQueryRepository itemQueryRepository) {
        this.itemRepository = itemRepository;
        this.itemQueryRepository = itemQueryRepository;
    }

    @Transactional
    public ItemResponse create(ItemRequest request) {
        if (itemRepository.existsByCodeValue(request.getCode())) {
            throw new BusinessException("Item code already exists");
        }
        Item item = new Item(request.getCode(), request.getName(), request.getUnit(), request.getItemType());
        item.update(request.getName(), request.getUnit(), request.getItemType(), request.isActive());
        return ItemResponse.from(itemRepository.save(item));
    }

    public List<ItemResponse> findAll() {
        return itemRepository.findAll().stream().map(ItemResponse::from).toList();
    }

    public List<ItemResponse> search(ItemType itemType, Boolean active, String keyword) {
        return itemQueryRepository.search(itemType, active, keyword).stream()
                .map(ItemResponse::from)
                .toList();
    }

    @Transactional
    public ItemResponse update(Long id, ItemRequest request) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new BusinessException("Item not found"));
        item.update(request.getName(), request.getUnit(), request.getItemType(), request.isActive());
        return ItemResponse.from(item);
    }
}
