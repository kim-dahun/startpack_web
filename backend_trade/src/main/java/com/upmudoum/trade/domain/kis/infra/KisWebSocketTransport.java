package com.upmudoum.trade.domain.kis.infra;

import java.util.function.Consumer;

public interface KisWebSocketTransport {

    void connect(String webSocketUrl, Consumer<String> messageConsumer);

    void send(String message);

    void close();

    boolean isOpen();
}
