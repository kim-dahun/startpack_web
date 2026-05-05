package com.upmudoum.trade.domain.master.repository;

import com.upmudoum.trade.domain.master.entity.TradeMasterImportLock;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TradeMasterImportLockRepository extends JpaRepository<TradeMasterImportLock, TradeMasterType> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select importLock from TradeMasterImportLock importLock where importLock.masterType = :masterType")
    Optional<TradeMasterImportLock> findByMasterTypeForUpdate(@Param("masterType") TradeMasterType masterType);
}
