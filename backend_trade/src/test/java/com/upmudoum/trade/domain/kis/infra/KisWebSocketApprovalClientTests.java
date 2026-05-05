package com.upmudoum.trade.domain.kis.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.trade.domain.kis.vo.KisProperties;
import java.util.Map;
import org.junit.jupiter.api.Test;

class KisWebSocketApprovalClientTests {

    @Test
    void extractApprovalKeyReadsKisResponse() {
        KisWebSocketApprovalClient client = new KisWebSocketApprovalClient(
                new ObjectMapper(),
                new KisProperties("app-key", "app-secret", "http://paper", "http://live", "ws://paper", "ws://live", "01"),
                "/oauth2/Approval"
        );

        assertThat(client.extractApprovalKey(Map.of("approval_key", "approval-key"))).isEqualTo("approval-key");
    }
}
