package com.upmudoum.trade.domain.account.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.upmudoum.trade.domain.account.dto.RegisterAccountRequest;
import com.upmudoum.trade.domain.account.repository.RegisteredAccountRepository;
import com.upmudoum.trade.domain.kis.vo.KisProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;

@DataJpaTest
@Import({
        RegisteredAccountService.class,
        RegisteredAccountServiceTests.TestConfig.class
})
class RegisteredAccountServiceTests {

    @Autowired
    private RegisteredAccountService service;

    @Autowired
    private RegisteredAccountRepository repository;

    @Test
    void registerUpsertsAccountMetadata() {
        service.register(request("12345678-01", "main", null, true));
        service.register(request("12345678-01", "main-updated", "02", true));

        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findByAccountNo("12345678-01"))
                .hasValueSatisfying(account -> {
                    assertThat(account.getAccountName()).isEqualTo("main-updated");
                    assertThat(account.getProductCode()).isEqualTo("02");
                    assertThat(account.isActive()).isTrue();
                });
    }

    @Test
    void inactiveAccountIsExcludedFromActiveLookup() {
        service.register(request("12345678-01", "main", null, false));

        assertThat(service.findActiveAccounts()).isEmpty();
        assertThat(service.findAll()).hasSize(1);
    }

    private RegisterAccountRequest request(String accountNo, String accountName, String productCode, boolean active) {
        RegisterAccountRequest request = new RegisterAccountRequest();
        request.setAccountNo(accountNo);
        request.setAccountName(accountName);
        request.setProductCode(productCode);
        request.setAliasName("alias");
        request.setMemo("memo");
        request.setActive(active);
        return request;
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        KisProperties kisProperties() {
            return new KisProperties("app-key", "app-secret", "http://paper", "http://live", "ws://paper", "ws://live", "01");
        }
    }
}
