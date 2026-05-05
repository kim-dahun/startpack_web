package com.upmudoum.trade.domain.kis.infra;

import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class KisRateLimiter {

    private static final long MIN_INTERVAL_MILLIS = Duration.ofSeconds(1).toMillis();

    private long lastCallStartedAt;

    public synchronized void acquire() {
        long now = System.currentTimeMillis();
        long waitMillis = MIN_INTERVAL_MILLIS - (now - lastCallStartedAt);
        if (waitMillis > 0) {
            sleep(waitMillis);
        }
        lastCallStartedAt = System.currentTimeMillis();
    }

    private void sleep(long waitMillis) {
        try {
            Thread.sleep(waitMillis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while waiting for KIS rate limit", ex);
        }
    }
}
