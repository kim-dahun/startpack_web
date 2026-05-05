package com.upmudoum.trade.domain.trade.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.upmudoum.trade.domain.kis.infra.KisQueryFactory;
import com.upmudoum.trade.domain.kis.infra.NoopKisRestClient;
import com.upmudoum.trade.domain.kis.repository.KisApiCallLogRepository;
import com.upmudoum.trade.domain.kis.service.KisApiCallLogService;
import com.upmudoum.trade.domain.kis.vo.KisProperties;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import com.upmudoum.trade.domain.trade.dto.OrderableAmountRequest;
import com.upmudoum.trade.domain.trade.dto.PlaceOrderRequest;
import com.upmudoum.trade.domain.trade.vo.TradeSide;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class KisOrderServiceTests {

    @Autowired
    private KisApiCallLogRepository repository;

    @Test
    void orderableAmountUsesKisNoopClientInTests() {
        KisOrderService service = service();
        OrderableAmountRequest request = new OrderableAmountRequest();
        request.setAccountNo("1234567801");
        request.setItemCode("005930");
        request.setPrice(new BigDecimal("70000"));
        request.setTradeMode(KisTradeMode.PAPER);

        assertThat(service.orderableAmount(request).getRaw()).containsEntry("mock", true);
    }

    @Test
    void cashOrderUsesKisNoopClientInTests() {
        KisOrderService service = service();
        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setAccountNo("1234567801");
        request.setItemCode("005930");
        request.setSide(TradeSide.BUY);
        request.setQuantity(1);
        request.setPrice(new BigDecimal("70000"));
        request.setTradeMode(KisTradeMode.PAPER);

        assertThat(service.placeOrder(request).getRaw()).containsEntry("mock", true);
    }

    private KisOrderService service() {
        KisApiCallLogService logService = new KisApiCallLogService(repository);
        return new KisOrderService(
                new NoopKisRestClient(logService),
                new KisQueryFactory(new KisProperties("app-key", "app-secret", "http://paper", "http://live", "ws://paper", "ws://live", "01"))
        );
    }
}
