package com.upmudoum.trade.domain.kis.infra;

import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import com.upmudoum.trade.domain.kis.vo.KisProperties;
import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeSubscriptionDto;
import com.upmudoum.trade.domain.marketdata.service.RealtimeReconnectHistoryService;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "trade.kis.realtime-client.enabled", havingValue = "true")
public class DefaultKisRealtimeClient implements KisRealtimeClient {

    private final KisWebSocketApprovalClient approvalClient;
    private final KisWebSocketFrameFactory frameFactory;
    private final KisWebSocketTransport transport;
    private final KisProperties kisProperties;
    private final KisRealtimeInboundHandler inboundHandler;
    private final RealtimeReconnectHistoryService reconnectHistoryService;
    private final Set<TradeRealtimeSubscriptionDto> subscriptions = ConcurrentHashMap.newKeySet();
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private volatile String approvalKey;

    public DefaultKisRealtimeClient(
            KisWebSocketApprovalClient approvalClient,
            KisWebSocketFrameFactory frameFactory,
            KisWebSocketTransport transport,
            KisProperties kisProperties,
            KisRealtimeInboundHandler inboundHandler,
            RealtimeReconnectHistoryService reconnectHistoryService
    ) {
        this.approvalClient = approvalClient;
        this.frameFactory = frameFactory;
        this.transport = transport;
        this.kisProperties = kisProperties;
        this.inboundHandler = inboundHandler;
        this.reconnectHistoryService = reconnectHistoryService;
    }

    @Override
    public void connect() {
        if (connected.compareAndSet(false, true)) {
            approvalKey = approvalClient.issueApprovalKey(KisTradeMode.LIVE);
            transport.connect(kisProperties.getWebSocketUrl(KisTradeMode.LIVE), inboundHandler::handle);
        }
    }

    @Override
    public void disconnect() {
        connected.set(false);
        transport.close();
    }

    @Override
    public void subscribe(TradeRealtimeSubscriptionDto subscription) {
        connect();
        subscriptions.add(subscription);
        transport.send(frameFactory.subscribe(approvalKey, subscription));
    }

    @Override
    public void unsubscribe(TradeRealtimeSubscriptionDto subscription) {
        subscriptions.remove(subscription);
        if (connected.get() && transport.isOpen()) {
            transport.send(frameFactory.unsubscribe(approvalKey, subscription));
        }
    }

    @Override
    public boolean isConnected() {
        return connected.get() && transport.isOpen();
    }

    public void reconnect() {
        int subscriptionCount = subscriptions.size();
        try {
            disconnect();
            connect();
            subscriptions.forEach(subscription -> transport.send(frameFactory.subscribe(approvalKey, subscription)));
            reconnectHistoryService.saveSuccess(subscriptionCount);
        } catch (RuntimeException ex) {
            reconnectHistoryService.saveFailure(subscriptionCount, ex);
            throw ex;
        }
    }

    String getApprovalKey() {
        return approvalKey;
    }

    Set<TradeRealtimeSubscriptionDto> subscriptions() {
        return Set.copyOf(subscriptions);
    }
}
