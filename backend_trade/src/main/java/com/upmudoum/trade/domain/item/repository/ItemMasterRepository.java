package com.upmudoum.trade.domain.item.repository;

import com.upmudoum.trade.domain.item.entity.ItemMaster;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface ItemMasterRepository extends JpaRepository<ItemMaster, Long> {

    Optional<ItemMaster> findByItemCode(String itemCode);

    long countByMasterType(TradeMasterType masterType);

    @Modifying
    void deleteByMasterType(TradeMasterType masterType);
}
