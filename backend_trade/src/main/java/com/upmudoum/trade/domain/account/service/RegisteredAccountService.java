package com.upmudoum.trade.domain.account.service;

import com.upmudoum.trade.domain.account.dto.RegisterAccountRequest;
import com.upmudoum.trade.domain.account.dto.RegisteredAccountDto;
import com.upmudoum.trade.domain.account.entity.RegisteredAccount;
import com.upmudoum.trade.domain.account.repository.RegisteredAccountRepository;
import com.upmudoum.trade.domain.kis.vo.KisProperties;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegisteredAccountService {

    private final RegisteredAccountRepository repository;
    private final KisProperties kisProperties;

    public RegisteredAccountService(RegisteredAccountRepository repository, KisProperties kisProperties) {
        this.repository = repository;
        this.kisProperties = kisProperties;
    }

    @Transactional(readOnly = true)
    public List<RegisteredAccountDto> findAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RegisteredAccount> findActiveAccounts() {
        return repository.findByActiveTrueOrderByCreatedAtDesc();
    }

    @Transactional
    public RegisteredAccountDto register(RegisterAccountRequest request) {
        RegisteredAccount account = repository.findByAccountNo(request.getAccountNo())
                .orElseGet(RegisteredAccount::new);
        Instant now = Instant.now();
        if (account.getCreatedAt() == null) {
            account.setCreatedAt(now);
        }
        account.setAccountNo(request.getAccountNo());
        account.setAccountName(request.getAccountName());
        account.setProductCode(firstNonBlank(request.getProductCode(), productCodeFromAccountNo(request.getAccountNo()), kisProperties.getAccountProductCode()));
        account.setAliasName(request.getAliasName());
        account.setMemo(request.getMemo());
        account.setActive(request.getActive() == null || request.getActive());
        account.setUpdatedAt(now);
        return toDto(repository.save(account));
    }

    @Transactional
    public RegisteredAccountDto update(Long id, RegisterAccountRequest request) {
        RegisteredAccount account = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "registered account not found"));
        account.setAccountNo(request.getAccountNo());
        account.setAccountName(request.getAccountName());
        account.setProductCode(firstNonBlank(request.getProductCode(), productCodeFromAccountNo(request.getAccountNo()), kisProperties.getAccountProductCode()));
        account.setAliasName(request.getAliasName());
        account.setMemo(request.getMemo());
        if (request.getActive() != null) {
            account.setActive(request.getActive());
        }
        account.setUpdatedAt(Instant.now());
        return toDto(repository.save(account));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private RegisteredAccountDto toDto(RegisteredAccount account) {
        RegisteredAccountDto dto = new RegisteredAccountDto();
        dto.setId(account.getId());
        dto.setAccountNo(account.getAccountNo());
        dto.setAccountName(account.getAccountName());
        dto.setProductCode(account.getProductCode());
        dto.setAliasName(account.getAliasName());
        dto.setMemo(account.getMemo());
        dto.setActive(account.isActive());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());
        return dto;
    }

    private String productCodeFromAccountNo(String accountNo) {
        if (accountNo == null) {
            return null;
        }
        String normalized = accountNo.trim().replace("-", "");
        if (normalized.length() > 8) {
            return normalized.substring(8);
        }
        return null;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }
}
