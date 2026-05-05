package com.upmudoum.trade.domain.trade.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.upmudoum.trade.domain.account.entity.AccountPositionSnapshot;
import com.upmudoum.trade.domain.account.entity.AccountSnapshot;
import com.upmudoum.trade.domain.account.repository.AccountPositionSnapshotRepository;
import com.upmudoum.trade.domain.account.repository.AccountSnapshotRepository;
import com.upmudoum.trade.domain.trade.dto.OrderValidationRequest;
import com.upmudoum.trade.domain.trade.dto.OrderValidationResultDto;
import com.upmudoum.trade.domain.trade.vo.TradeSide;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class OrderValidationServiceTests {

    @Autowired
    private AccountSnapshotRepository accountSnapshotRepository;

    @Autowired
    private AccountPositionSnapshotRepository accountPositionSnapshotRepository;

    @Test
    void buyValidationAllowsWhenLatestCashCoversRequiredAmount() {
        saveAccount("12345678", "100000");
        OrderValidationService service = new OrderValidationService(accountSnapshotRepository, accountPositionSnapshotRepository);

        OrderValidationResultDto result = service.validate(request(TradeSide.BUY, 2, "40000"));

        assertThat(result.isAllowed()).isTrue();
        assertThat(result.getRequiredAmount()).isEqualByComparingTo("80000");
        assertThat(result.getAvailableCashAmount()).isEqualByComparingTo("100000");
    }

    @Test
    void buyValidationRejectsWhenCashIsNotEnough() {
        saveAccount("12345678", "50000");
        OrderValidationService service = new OrderValidationService(accountSnapshotRepository, accountPositionSnapshotRepository);

        OrderValidationResultDto result = service.validate(request(TradeSide.BUY, 2, "40000"));

        assertThat(result.isAllowed()).isFalse();
        assertThat(result.getFailureReason()).isEqualTo("insufficient cash amount");
    }

    @Test
    void sellValidationUsesLatestPositionSnapshotOrderableQuantity() {
        savePosition("12345678", "005930", 10, 7);
        OrderValidationService service = new OrderValidationService(accountSnapshotRepository, accountPositionSnapshotRepository);
        OrderValidationRequest request = request(TradeSide.SELL, 3, "40000");
        request.setAvailableQuantity(0L);

        OrderValidationResultDto result = service.validate(request);

        assertThat(result.isAllowed()).isTrue();
        assertThat(result.getAvailableQuantity()).isEqualTo(7);
    }

    @Test
    void sellValidationRejectsWhenLatestPositionSnapshotQuantityIsNotEnough() {
        savePosition("12345678", "005930", 10, 2);
        OrderValidationService service = new OrderValidationService(accountSnapshotRepository, accountPositionSnapshotRepository);
        OrderValidationRequest request = request(TradeSide.SELL, 3, "40000");
        request.setAvailableQuantity(100L);

        OrderValidationResultDto result = service.validate(request);

        assertThat(result.isAllowed()).isFalse();
        assertThat(result.getFailureReason()).isEqualTo("insufficient available quantity");
    }

    @Test
    void sellValidationRejectsWhenPositionSnapshotIsMissing() {
        OrderValidationService service = new OrderValidationService(accountSnapshotRepository, accountPositionSnapshotRepository);

        OrderValidationResultDto result = service.validate(request(TradeSide.SELL, 3, "40000"));

        assertThat(result.isAllowed()).isFalse();
        assertThat(result.getFailureReason()).isEqualTo("position snapshot is required for sell validation");
    }

    private OrderValidationRequest request(TradeSide side, long quantity, String price) {
        OrderValidationRequest request = new OrderValidationRequest();
        request.setAccountNo("12345678");
        request.setItemCode("005930");
        request.setSide(side);
        request.setQuantity(quantity);
        request.setPrice(new BigDecimal(price));
        return request;
    }

    private void saveAccount(String accountNo, String cashAmount) {
        AccountSnapshot snapshot = new AccountSnapshot();
        snapshot.setAccountNo(accountNo);
        snapshot.setAccountName("test-account");
        snapshot.setTotalAssetAmount(new BigDecimal(cashAmount));
        snapshot.setCashAmount(new BigDecimal(cashAmount));
        snapshot.setCapturedAt(Instant.now());
        accountSnapshotRepository.save(snapshot);
    }

    private void savePosition(String accountNo, String itemCode, long quantity, long orderableQuantity) {
        AccountPositionSnapshot snapshot = new AccountPositionSnapshot();
        snapshot.setAccountNo(accountNo);
        snapshot.setItemCode(itemCode);
        snapshot.setItemName("Samsung Electronics");
        snapshot.setQuantity(quantity);
        snapshot.setOrderableQuantity(orderableQuantity);
        snapshot.setAveragePrice(BigDecimal.TEN);
        snapshot.setCurrentPrice(BigDecimal.TEN);
        snapshot.setEvaluationAmount(BigDecimal.TEN);
        snapshot.setProfitLossAmount(BigDecimal.ZERO);
        snapshot.setProfitLossRate(BigDecimal.ZERO);
        snapshot.setCapturedAt(Instant.now());
        accountPositionSnapshotRepository.save(snapshot);
    }
}
