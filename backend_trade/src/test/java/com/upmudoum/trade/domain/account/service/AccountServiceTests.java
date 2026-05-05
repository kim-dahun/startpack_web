package com.upmudoum.trade.domain.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.upmudoum.trade.domain.account.dto.AccountSummaryDto;
import com.upmudoum.trade.domain.account.dto.PositionDto;
import com.upmudoum.trade.domain.account.entity.RegisteredAccount;
import com.upmudoum.trade.domain.account.infra.KisAccountMapper;
import com.upmudoum.trade.domain.kis.infra.KisQueryFactory;
import com.upmudoum.trade.domain.kis.infra.KisRestClient;
import com.upmudoum.trade.domain.kis.vo.KisEndpoint;
import com.upmudoum.trade.domain.kis.vo.KisProperties;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class AccountServiceTests {

    @Test
    void findAccountsUsesConfiguredRealAccountNumbers() {
        CapturingKisRestClient kisRestClient = new CapturingKisRestClient();
        AccountSnapshotService snapshotService = mock(AccountSnapshotService.class);
        AccountService service = service("12345678-01", kisRestClient, snapshotService, List.of());

        List<AccountSummaryDto> accounts = service.findAccounts(KisTradeMode.LIVE);

        assertThat(accounts).hasSize(1);
        assertThat(accounts.getFirst().getAccountNo()).isEqualTo("12345678-01");
        assertThat(accounts.getFirst().getTotalAssetAmount()).isEqualByComparingTo("10000");
        assertThat(kisRestClient.endpoint).isEqualTo(KisEndpoint.INQUIRE_BALANCE);
        assertThat(kisRestClient.query)
                .containsEntry("CANO", "12345678")
                .containsEntry("ACNT_PRDT_CD", "01");
        verify(snapshotService).saveAccounts(anyList());
    }

    @Test
    void findAccountsRejectsMissingConfiguredAccountNumbers() {
        AccountService service = service("", new CapturingKisRestClient(), mock(AccountSnapshotService.class), List.of());

        assertThatThrownBy(() -> service.findAccounts(KisTradeMode.LIVE))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400 BAD_REQUEST");
    }

    @Test
    void findAccountsUsesRequestAccountNoWhenConfigIsMissing() {
        CapturingKisRestClient kisRestClient = new CapturingKisRestClient();
        AccountSnapshotService snapshotService = mock(AccountSnapshotService.class);
        AccountService service = service("", kisRestClient, snapshotService, List.of());

        List<AccountSummaryDto> accounts = service.findAccounts(KisTradeMode.LIVE, "12345678-01", null);

        assertThat(accounts).hasSize(1);
        assertThat(kisRestClient.query)
                .containsEntry("CANO", "12345678")
                .containsEntry("ACNT_PRDT_CD", "01");
    }

    @Test
    void findAccountsUsesRequestAccountNumbersWhenConfigIsMissing() {
        CapturingKisRestClient kisRestClient = new CapturingKisRestClient();
        AccountSnapshotService snapshotService = mock(AccountSnapshotService.class);
        AccountService service = service("", kisRestClient, snapshotService, List.of());

        List<AccountSummaryDto> accounts = service.findAccounts(KisTradeMode.LIVE, null, "12345678-01,87654321-01");

        assertThat(accounts).hasSize(2);
    }

    @Test
    void findAccountsUsesRegisteredAccountsWhenRequestAndConfigAreMissing() {
        CapturingKisRestClient kisRestClient = new CapturingKisRestClient();
        AccountSnapshotService snapshotService = mock(AccountSnapshotService.class);
        AccountService service = service("", kisRestClient, snapshotService, List.of(registeredAccount("22223333-01")));

        List<AccountSummaryDto> accounts = service.findAccounts(KisTradeMode.LIVE);

        assertThat(accounts).hasSize(1);
        assertThat(accounts.getFirst().getAccountNo()).isEqualTo("22223333-01");
        assertThat(kisRestClient.query)
                .containsEntry("CANO", "22223333")
                .containsEntry("ACNT_PRDT_CD", "01");
    }

    @Test
    void findPositionsUsesKisBalanceAndSavesSnapshots() {
        CapturingKisRestClient kisRestClient = new CapturingKisRestClient();
        AccountSnapshotService snapshotService = mock(AccountSnapshotService.class);
        AccountService service = service("12345678-01", kisRestClient, snapshotService, List.of());

        List<PositionDto> positions = service.findPositions("12345678-01", KisTradeMode.LIVE);

        assertThat(positions).hasSize(1);
        assertThat(positions.getFirst().getItemCode()).isEqualTo("005930");
        assertThat(positions.getFirst().getQuantity()).isEqualTo(10);
        assertThat(positions.getFirst().getOrderableQuantity()).isEqualTo(7);
        assertThat(kisRestClient.query)
                .containsEntry("CANO", "12345678")
                .containsEntry("ACNT_PRDT_CD", "01");
        verify(snapshotService).savePositions(anyList());
    }

    private AccountService service(
            String accountNumbers,
            KisRestClient kisRestClient,
            AccountSnapshotService snapshotService,
            List<RegisteredAccount> registeredAccounts
    ) {
        KisProperties properties = new KisProperties(
                "app-key",
                "app-secret",
                "http://paper",
                "http://live",
                "ws://paper",
                "ws://live",
                "01",
                accountNumbers
        );
        RegisteredAccountService registeredAccountService = mock(RegisteredAccountService.class);
        when(registeredAccountService.findActiveAccounts()).thenReturn(registeredAccounts);
        return new AccountService(
                kisRestClient,
                new KisAccountMapper(),
                new KisQueryFactory(properties),
                snapshotService,
                properties,
                registeredAccountService
        );
    }

    private RegisteredAccount registeredAccount(String accountNo) {
        RegisteredAccount account = new RegisteredAccount();
        account.setAccountNo(accountNo);
        account.setAccountName("registered");
        account.setProductCode("01");
        account.setActive(true);
        return account;
    }

    private static class CapturingKisRestClient implements KisRestClient {

        private KisEndpoint endpoint;
        private Map<String, String> query;

        @Override
        public Map<String, Object> get(String endpoint, Map<String, String> query, KisTradeMode tradeMode) {
            throw new UnsupportedOperationException("Use KisEndpoint overload");
        }

        @Override
        public Map<String, Object> get(KisEndpoint endpoint, Map<String, String> query, KisTradeMode tradeMode) {
            this.endpoint = endpoint;
            this.query = query;
            return Map.of("output2", Map.of(
                    "tot_evlu_amt", "10000",
                    "dnca_tot_amt", "3000"
            ), "output1", List.of(Map.of(
                    "pdno", "005930",
                    "prdt_name", "Samsung Electronics",
                    "hldg_qty", "10",
                    "ord_psbl_qty", "7",
                    "pchs_avg_pric", "70000",
                    "prpr", "72000",
                    "evlu_amt", "720000",
                    "evlu_pfls_amt", "20000",
                    "evlu_pfls_rt", "2.85"
            )));
        }

        @Override
        public Map<String, Object> post(KisEndpoint endpoint, Map<String, String> body, KisTradeMode tradeMode) {
            throw new UnsupportedOperationException("Not used");
        }
    }
}
