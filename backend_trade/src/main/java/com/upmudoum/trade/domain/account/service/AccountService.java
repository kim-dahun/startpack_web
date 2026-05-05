package com.upmudoum.trade.domain.account.service;

import com.upmudoum.trade.domain.account.dto.AccountBalanceDetailDto;
import com.upmudoum.trade.domain.account.dto.AccountSummaryDto;
import com.upmudoum.trade.domain.account.dto.DailyBalanceDto;
import com.upmudoum.trade.domain.account.dto.PositionDto;
import com.upmudoum.trade.domain.account.entity.RegisteredAccount;
import com.upmudoum.trade.domain.account.infra.KisAccountMapper;
import com.upmudoum.trade.domain.kis.infra.KisQueryFactory;
import com.upmudoum.trade.domain.kis.infra.KisRestClient;
import com.upmudoum.trade.domain.kis.vo.KisEndpoint;
import com.upmudoum.trade.domain.kis.vo.KisProperties;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountService {

    private final KisRestClient kisRestClient;
    private final KisAccountMapper kisAccountMapper;
    private final KisQueryFactory kisQueryFactory;
    private final AccountSnapshotService accountSnapshotService;
    private final KisProperties kisProperties;
    private final RegisteredAccountService registeredAccountService;

    public AccountService(
            KisRestClient kisRestClient,
            KisAccountMapper kisAccountMapper,
            KisQueryFactory kisQueryFactory,
            AccountSnapshotService accountSnapshotService,
            KisProperties kisProperties,
            RegisteredAccountService registeredAccountService
    ) {
        this.kisRestClient = kisRestClient;
        this.kisAccountMapper = kisAccountMapper;
        this.kisQueryFactory = kisQueryFactory;
        this.accountSnapshotService = accountSnapshotService;
        this.kisProperties = kisProperties;
        this.registeredAccountService = registeredAccountService;
    }

    public List<AccountSummaryDto> findAccounts(KisTradeMode tradeMode) {
        return findAccounts(tradeMode, null, null);
    }

    public List<AccountSummaryDto> findAccounts(KisTradeMode tradeMode, String accountNo, String requestedAccountNumbers) {
        List<String> accountNumbers = accountNumbers(accountNo, requestedAccountNumbers);
        if (accountNumbers.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "KIS account number is required. Set KIS_ACCOUNT_NO/KIS_ACCOUNT_NUMBERS in param.env or pass accountNo/accountNumbers query parameter."
            );
        }
        List<AccountSummaryDto> accounts = accountNumbers.stream()
                .map(targetAccountNo -> kisAccountMapper.toAccountSummary(
                        targetAccountNo,
                        kisRestClient.get(KisEndpoint.INQUIRE_BALANCE, kisQueryFactory.balance(targetAccountNo), tradeMode)
                ))
                .toList();
        accountSnapshotService.saveAccounts(accounts);
        return accounts;
    }

    private List<String> accountNumbers(String accountNo, String requestedAccountNumbers) {
        String source = firstNonBlank(requestedAccountNumbers, accountNo);
        if (source == null) {
            List<String> registeredAccountNumbers = registeredAccountService.findActiveAccounts().stream()
                    .map(RegisteredAccount::getAccountNo)
                    .toList();
            if (!registeredAccountNumbers.isEmpty()) {
                return registeredAccountNumbers;
            }
            return kisProperties.getAccountNumbers();
        }
        return Arrays.stream(source.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    public AccountBalanceDetailDto findBalance(String accountNo, KisTradeMode tradeMode) {
        AccountBalanceDetailDto detail = kisAccountMapper.toBalanceDetail(
                accountNo,
                kisRestClient.get(KisEndpoint.INQUIRE_BALANCE, kisQueryFactory.balance(accountNo), tradeMode)
        );
        if (detail.getPositions() != null && !detail.getPositions().isEmpty()) {
            accountSnapshotService.savePositions(detail.getPositions());
        }
        return detail;
    }

    public List<PositionDto> findPositions(String accountNo, KisTradeMode tradeMode) {
        List<PositionDto> positions = kisAccountMapper.toPositions(
                accountNo,
                kisRestClient.get(KisEndpoint.INQUIRE_BALANCE, kisQueryFactory.balance(accountNo), tradeMode)
        );
        if (!positions.isEmpty()) {
            accountSnapshotService.savePositions(positions);
        }
        return positions;
    }

    public List<DailyBalanceDto> findDailyBalances(String accountNo, LocalDate baseDate, KisTradeMode tradeMode) {
        List<DailyBalanceDto> balances = kisAccountMapper.toDailyBalances(
                accountNo,
                baseDate,
                kisRestClient.get(KisEndpoint.INQUIRE_DAILY_CCLD, kisQueryFactory.dailyCcld(accountNo, baseDate), tradeMode)
        );
        if (!balances.isEmpty()) {
            accountSnapshotService.saveDailyBalances(balances);
        }
        return balances;
    }
}
