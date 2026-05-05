package com.upmudoum.trade.domain.account.service;

import com.upmudoum.trade.domain.account.dto.AccountSummaryDto;
import com.upmudoum.trade.domain.account.dto.DailyBalanceDto;
import com.upmudoum.trade.domain.account.dto.PositionDto;
import com.upmudoum.trade.domain.account.entity.AccountPositionSnapshot;
import com.upmudoum.trade.domain.account.entity.AccountSnapshot;
import com.upmudoum.trade.domain.account.entity.DailyBalanceSnapshot;
import com.upmudoum.trade.domain.account.repository.AccountPositionSnapshotRepository;
import com.upmudoum.trade.domain.account.repository.AccountSnapshotRepository;
import com.upmudoum.trade.domain.account.repository.DailyBalanceSnapshotRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountSnapshotService {

    private final AccountSnapshotRepository accountSnapshotRepository;
    private final DailyBalanceSnapshotRepository dailyBalanceSnapshotRepository;
    private final AccountPositionSnapshotRepository accountPositionSnapshotRepository;

    public AccountSnapshotService(
            AccountSnapshotRepository accountSnapshotRepository,
            DailyBalanceSnapshotRepository dailyBalanceSnapshotRepository,
            AccountPositionSnapshotRepository accountPositionSnapshotRepository
    ) {
        this.accountSnapshotRepository = accountSnapshotRepository;
        this.dailyBalanceSnapshotRepository = dailyBalanceSnapshotRepository;
        this.accountPositionSnapshotRepository = accountPositionSnapshotRepository;
    }

    @Transactional
    public void saveAccounts(List<AccountSummaryDto> accounts) {
        Instant now = Instant.now();
        accounts.forEach(account -> {
            AccountSnapshot snapshot = new AccountSnapshot();
            snapshot.setAccountNo(account.getAccountNo());
            snapshot.setAccountName(account.getAccountName());
            snapshot.setTotalAssetAmount(account.getTotalAssetAmount());
            snapshot.setCashAmount(account.getCashAmount());
            snapshot.setCapturedAt(now);
            accountSnapshotRepository.save(snapshot);
        });
    }

    @Transactional
    public void saveDailyBalances(List<DailyBalanceDto> balances) {
        Instant now = Instant.now();
        balances.forEach(balance -> {
            DailyBalanceSnapshot snapshot = new DailyBalanceSnapshot();
            snapshot.setAccountNo(balance.getAccountNo());
            snapshot.setBaseDate(balance.getBaseDate());
            snapshot.setTotalAssetAmount(balance.getTotalAssetAmount());
            snapshot.setProfitLossAmount(balance.getProfitLossAmount());
            snapshot.setCapturedAt(now);
            dailyBalanceSnapshotRepository.save(snapshot);
        });
    }

    @Transactional
    public void savePositions(List<PositionDto> positions) {
        Instant now = Instant.now();
        positions.forEach(position -> {
            AccountPositionSnapshot snapshot = new AccountPositionSnapshot();
            snapshot.setAccountNo(position.getAccountNo());
            snapshot.setItemCode(position.getItemCode());
            snapshot.setItemName(position.getItemName());
            snapshot.setQuantity(position.getQuantity());
            snapshot.setOrderableQuantity(position.getOrderableQuantity());
            snapshot.setAveragePrice(position.getAveragePrice());
            snapshot.setCurrentPrice(position.getCurrentPrice());
            snapshot.setEvaluationAmount(position.getEvaluationAmount());
            snapshot.setProfitLossAmount(position.getProfitLossAmount());
            snapshot.setProfitLossRate(position.getProfitLossRate());
            snapshot.setCapturedAt(now);
            accountPositionSnapshotRepository.save(snapshot);
        });
    }
}
