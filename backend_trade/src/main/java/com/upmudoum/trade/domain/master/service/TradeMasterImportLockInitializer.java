package com.upmudoum.trade.domain.master.service;

import com.upmudoum.trade.domain.master.entity.TradeMasterImportLock;
import com.upmudoum.trade.domain.master.repository.TradeMasterImportLockRepository;
import com.upmudoum.trade.domain.master.vo.TradeMasterImportStatus;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import java.time.Instant;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class TradeMasterImportLockInitializer {

    private final TradeMasterImportLockRepository lockRepository;

    public TradeMasterImportLockInitializer(TradeMasterImportLockRepository lockRepository) {
        this.lockRepository = lockRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeLocks() {
        for (TradeMasterType masterType : TradeMasterType.values()) {
            initializeLock(masterType);
        }
    }

    private void initializeLock(TradeMasterType masterType) {
        if (lockRepository.existsById(masterType)) {
            return;
        }
        try {
            lockRepository.saveAndFlush(newLock(masterType));
        } catch (DataIntegrityViolationException ignored) {
            // Another instance can create the row first during parallel startup.
        }
    }

    private TradeMasterImportLock newLock(TradeMasterType masterType) {
        TradeMasterImportLock lock = new TradeMasterImportLock();
        lock.setMasterType(masterType);
        lock.setImportStatus(TradeMasterImportStatus.SUCCESS);
        lock.setLastRequestedAt(Instant.EPOCH);
        lock.setUpdatedAt(Instant.EPOCH);
        return lock;
    }
}
