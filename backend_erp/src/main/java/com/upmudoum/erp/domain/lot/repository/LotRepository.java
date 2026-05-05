package com.upmudoum.erp.domain.lot.repository;

import com.upmudoum.erp.domain.lot.entity.Lot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LotRepository extends JpaRepository<Lot, Long> {

    Optional<Lot> findByItemIdAndLotNo(Long itemId, String lotNo);

    List<Lot> findByItemIdAndEnabledTrueOrderByIdDesc(Long itemId);
}
