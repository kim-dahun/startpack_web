package com.upmudoum.erp.domain.item.repository;

import com.upmudoum.erp.domain.item.entity.Item;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByCodeValue(String code);

    boolean existsByCodeValue(String code);
}
