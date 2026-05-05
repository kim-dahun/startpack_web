package com.upmudoum.trade.domain.kis.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.upmudoum.trade.domain.kis.repository.KisApiCallLogRepository;
import com.upmudoum.trade.domain.kis.service.KisApiCallLogService;
import com.upmudoum.trade.domain.kis.vo.KisEndpoint;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class NoopKisRestClientTests {

    @Autowired
    private KisApiCallLogRepository repository;

    @Test
    void getStoresCallLog() {
        KisApiCallLogService logService = new KisApiCallLogService(repository);
        NoopKisRestClient client = new NoopKisRestClient(logService);

        Map<String, Object> response = client.get("/test", Map.of("itemCode", "005930"), KisTradeMode.PAPER);

        assertThat(response).containsEntry("mock", true);
        assertThat(logService.findAll())
                .hasSize(1)
                .first()
                .extracting("endpoint", "statusCode")
                .containsExactly("/test", 200);
    }

    @Test
    void postStoresCallLogWithoutCallingRealKis() {
        KisApiCallLogService logService = new KisApiCallLogService(repository);
        NoopKisRestClient client = new NoopKisRestClient(logService);

        Map<String, Object> response = client.post(KisEndpoint.ORDER_CASH_BUY, Map.of("PDNO", "005930"), KisTradeMode.PAPER);

        assertThat(response).containsEntry("mock", true);
        assertThat(logService.findAll())
                .hasSize(1)
                .first()
                .extracting("endpoint", "method", "statusCode")
                .containsExactly(KisEndpoint.ORDER_CASH_BUY.getPath(), "POST", 200);
    }
}
