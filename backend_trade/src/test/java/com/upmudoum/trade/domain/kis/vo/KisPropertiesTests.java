package com.upmudoum.trade.domain.kis.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class KisPropertiesTests {

    @Test
    void getBaseUrlReturnsModeSpecificUrl() {
        KisProperties properties = new KisProperties("app-key", "app-secret", "http://paper", "http://live", "ws://paper", "ws://live", "01");

        assertThat(properties.getBaseUrl(KisTradeMode.PAPER)).isEqualTo("http://paper");
        assertThat(properties.getBaseUrl(KisTradeMode.LIVE)).isEqualTo("http://live");
        assertThat(properties.getWebSocketUrl(KisTradeMode.PAPER)).isEqualTo("ws://paper");
        assertThat(properties.getWebSocketUrl(KisTradeMode.LIVE)).isEqualTo("ws://live");
    }

    @Test
    void validateCredentialsRequiresAppKeyAndSecret() {
        KisProperties properties = new KisProperties("", "", "http://paper", "http://live", "ws://paper", "ws://live", "01");

        assertThatThrownBy(properties::validateCredentials)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("APP_KEY");
    }
}
