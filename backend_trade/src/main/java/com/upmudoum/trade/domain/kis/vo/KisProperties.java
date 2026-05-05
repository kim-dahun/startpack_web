package com.upmudoum.trade.domain.kis.vo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.Getter;

@Getter
@Component
public class KisProperties {

    private final String appKey;
    private final String appSecretKey;
    private final String paperBaseUrl;
    private final String liveBaseUrl;
    private final String paperWebSocketUrl;
    private final String liveWebSocketUrl;
    private final String accountProductCode;
    private final String accountNumbers;

    public KisProperties(
            String appKey,
            String appSecretKey,
            String paperBaseUrl,
            String liveBaseUrl,
            String paperWebSocketUrl,
            String liveWebSocketUrl,
            String accountProductCode
    ) {
        this(appKey, appSecretKey, paperBaseUrl, liveBaseUrl, paperWebSocketUrl, liveWebSocketUrl, accountProductCode, "");
    }

    @Autowired
    public KisProperties(
            @Value("${trade.kis.app-key:}") String appKey,
            @Value("${trade.kis.app-secret-key:}") String appSecretKey,
            @Value("${trade.kis.paper-base-url}") String paperBaseUrl,
            @Value("${trade.kis.live-base-url}") String liveBaseUrl,
            @Value("${trade.kis.paper-websocket-url}") String paperWebSocketUrl,
            @Value("${trade.kis.live-websocket-url}") String liveWebSocketUrl,
            @Value("${trade.kis.account-product-code:01}") String accountProductCode,
            @Value("${trade.kis.account-numbers:}") String accountNumbers
    ) {
        this.appKey = appKey;
        this.appSecretKey = appSecretKey;
        this.paperBaseUrl = paperBaseUrl;
        this.liveBaseUrl = liveBaseUrl;
        this.paperWebSocketUrl = paperWebSocketUrl;
        this.liveWebSocketUrl = liveWebSocketUrl;
        this.accountProductCode = accountProductCode;
        this.accountNumbers = accountNumbers;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getAppSecretKey() {
        return appSecretKey;
    }

    public String getBaseUrl(KisTradeMode tradeMode) {
        if (tradeMode == KisTradeMode.LIVE) {
            return liveBaseUrl;
        }
        return paperBaseUrl;
    }

    public String getWebSocketUrl(KisTradeMode tradeMode) {
        if (tradeMode == KisTradeMode.LIVE) {
            return liveWebSocketUrl;
        }
        return paperWebSocketUrl;
    }

    public String getAccountProductCode() {
        return accountProductCode;
    }

    public java.util.List<String> getAccountNumbers() {
        if (accountNumbers == null || accountNumbers.isBlank()) {
            return java.util.List.of();
        }
        return java.util.Arrays.stream(accountNumbers.split(","))
                .map(String::trim)
                .filter(accountNo -> !accountNo.isBlank())
                .toList();
    }

    public void validateCredentials() {
        if (appKey == null || appKey.isBlank()) {
            throw new IllegalStateException("trade.kis.app-key is required. Check APP_KEY in param.env.");
        }
        if (appSecretKey == null || appSecretKey.isBlank()) {
            throw new IllegalStateException("trade.kis.app-secret-key is required. Check APP_SECRET_KEY in param.env.");
        }
    }
}
