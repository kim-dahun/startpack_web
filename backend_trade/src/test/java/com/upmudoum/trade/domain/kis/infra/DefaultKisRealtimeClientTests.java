package com.upmudoum.trade.domain.kis.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import com.upmudoum.trade.domain.kis.vo.KisProperties;
import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeSubscriptionDto;
import com.upmudoum.trade.domain.marketdata.service.RealtimeReconnectHistoryService;
import com.upmudoum.trade.domain.marketdata.vo.TradeRealtimeEventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DefaultKisRealtimeClientTests {

    @Test
    void reconnectKeepsSubscriptionsAndRefreshesApprovalKey() {
        StubApprovalClient approvalClient = new StubApprovalClient();
        CapturingTransport transport = new CapturingTransport();
        RealtimeReconnectHistoryService reconnectHistoryService = Mockito.mock(RealtimeReconnectHistoryService.class);
        DefaultKisRealtimeClient client = new DefaultKisRealtimeClient(
                approvalClient,
                new KisWebSocketFrameFactory(new ObjectMapper()),
                transport,
                new KisProperties("app-key", "app-secret", "http://paper", "http://live", "ws://paper", "ws://live", "01"),
                Mockito.mock(KisRealtimeInboundHandler.class),
                reconnectHistoryService
        );
        TradeRealtimeSubscriptionDto subscription = new TradeRealtimeSubscriptionDto(TradeRealtimeEventType.PRICE, "005930");

        client.subscribe(subscription);
        String firstApprovalKey = client.getApprovalKey();
        client.reconnect();

        assertThat(firstApprovalKey).isEqualTo("approval-1");
        assertThat(client.getApprovalKey()).isEqualTo("approval-2");
        assertThat(client.subscriptions()).containsExactly(subscription);
        assertThat(transport.urls).containsExactly("ws://live", "ws://live");
        assertThat(transport.messages).hasSize(2);
        Mockito.verify(reconnectHistoryService).saveSuccess(1);
    }

    private static class StubApprovalClient extends KisWebSocketApprovalClient {

        private int sequence = 0;

        StubApprovalClient() {
            super(null, null, "/oauth2/Approval");
        }

        @Override
        public String issueApprovalKey(KisTradeMode tradeMode) {
            sequence++;
            return "approval-" + sequence;
        }
    }

    private static class CapturingTransport implements KisWebSocketTransport {

        private final List<String> urls = new ArrayList<>();
        private final List<String> messages = new ArrayList<>();
        private boolean open;

        @Override
        public void connect(String webSocketUrl, Consumer<String> messageConsumer) {
            urls.add(webSocketUrl);
            open = true;
        }

        @Override
        public void send(String message) {
            messages.add(message);
        }

        @Override
        public void close() {
            open = false;
        }

        @Override
        public boolean isOpen() {
            return open;
        }
    }
}
