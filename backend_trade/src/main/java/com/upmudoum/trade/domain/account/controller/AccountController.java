package com.upmudoum.trade.domain.account.controller;

import com.upmudoum.trade.domain.account.dto.AccountSummaryDto;
import com.upmudoum.trade.domain.account.dto.AccountBalanceDetailDto;
import com.upmudoum.trade.domain.account.dto.DailyBalanceDto;
import com.upmudoum.trade.domain.account.dto.PositionDto;
import com.upmudoum.trade.domain.account.dto.RegisterAccountRequest;
import com.upmudoum.trade.domain.account.dto.RegisteredAccountDto;
import com.upmudoum.trade.domain.account.service.AccountService;
import com.upmudoum.trade.domain.account.service.RegisteredAccountService;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/accounts")
public class AccountController {

    private final AccountService accountService;
    private final RegisteredAccountService registeredAccountService;

    public AccountController(AccountService accountService, RegisteredAccountService registeredAccountService) {
        this.accountService = accountService;
        this.registeredAccountService = registeredAccountService;
    }

    @GetMapping
    public List<AccountSummaryDto> accounts(
            @RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode,
            @RequestParam(required = false) String accountNo,
            @RequestParam(required = false) String accountNumbers
    ) {
        return accountService.findAccounts(tradeMode, accountNo, accountNumbers);
    }

    @GetMapping("/registered")
    public List<RegisteredAccountDto> registeredAccounts() {
        return registeredAccountService.findAll();
    }

    @PostMapping("/registered")
    public RegisteredAccountDto registerAccount(@Valid @RequestBody RegisterAccountRequest request) {
        return registeredAccountService.register(request);
    }

    @PatchMapping("/registered/{id}")
    public RegisteredAccountDto updateAccount(@PathVariable Long id, @Valid @RequestBody RegisterAccountRequest request) {
        return registeredAccountService.update(id, request);
    }

    @DeleteMapping("/registered/{id}")
    public void deleteAccount(@PathVariable Long id) {
        registeredAccountService.delete(id);
    }

    @GetMapping("/{accountNo}/balances")
    public AccountBalanceDetailDto balance(
            @org.springframework.web.bind.annotation.PathVariable String accountNo,
            @RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode
    ) {
        return accountService.findBalance(accountNo, tradeMode);
    }

    @GetMapping("/{accountNo}/positions")
    public List<PositionDto> positions(
            @org.springframework.web.bind.annotation.PathVariable String accountNo,
            @RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode
    ) {
        return accountService.findPositions(accountNo, tradeMode);
    }

    @GetMapping("/daily-balances")
    public List<DailyBalanceDto> dailyBalances(
            @RequestParam String accountNo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baseDate,
            @RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode
    ) {
        return accountService.findDailyBalances(accountNo, baseDate, tradeMode);
    }
}
