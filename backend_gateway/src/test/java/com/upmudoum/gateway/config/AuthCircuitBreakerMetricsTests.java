package com.upmudoum.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "gateway.routes.services.auth-url=http://localhost:18081",
        "gateway.routes.services.user-url=http://localhost:18082",
        "gateway.routes.services.erp-url=http://localhost:18083",
        "gateway.routes.services.groupware-url=http://localhost:18084",
        "gateway.routes.services.trade-url=http://localhost:18085",
        "gateway.auth.verify-path=/api/v1/auth/tokens/verify",
        "gateway.auth.timeout=3s",
        "gateway.cors.allowed-origins=http://localhost:3000"
})
class AuthCircuitBreakerMetricsTests {

    @Autowired
    private CircuitBreaker authVerificationCircuitBreaker;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    void circuitBreakerMetricsAreRegistered() {
        authVerificationCircuitBreaker.onSuccess(10, TimeUnit.MILLISECONDS);

        boolean hasCircuitBreakerMetric = meterRegistry.getMeters().stream()
                .anyMatch(meter -> meter.getId().getName().startsWith("resilience4j.circuitbreaker"));

        assertThat(hasCircuitBreakerMetric).isTrue();
    }
}
