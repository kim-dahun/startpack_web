package com.upmudoum.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthCircuitBreakerConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AuthCircuitBreakerConfiguration.class);

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry(GatewayProperties properties) {
        GatewayProperties.CircuitBreaker authCircuitBreaker = properties.getAuth().getCircuitBreaker();
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(authCircuitBreaker.getFailureRateThreshold())
                .slidingWindowSize(authCircuitBreaker.getSlidingWindowSize())
                .permittedNumberOfCallsInHalfOpenState(authCircuitBreaker.getPermittedNumberOfCallsInHalfOpenState())
                .waitDurationInOpenState(authCircuitBreaker.getWaitDurationInOpenState())
                .build();
        return CircuitBreakerRegistry.of(config);
    }

    @Bean
    public CircuitBreaker authVerificationCircuitBreaker(
            CircuitBreakerRegistry circuitBreakerRegistry,
            GatewayProperties properties
    ) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry
                .circuitBreaker(properties.getAuth().getCircuitBreaker().getName());
        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> log.warn(
                        "auth circuit breaker state transition name={} from={} to={}",
                        event.getCircuitBreakerName(),
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()
                ))
                .onError(event -> log.warn(
                        "auth circuit breaker error name={} durationMs={} failure={}",
                        event.getCircuitBreakerName(),
                        event.getElapsedDuration().toMillis(),
                        event.getThrowable().getClass().getSimpleName()
                ))
                .onCallNotPermitted(event -> log.warn(
                        "auth circuit breaker call not permitted name={}",
                        event.getCircuitBreakerName()
                ));
        return circuitBreaker;
    }

    @Bean
    public MeterRegistryCustomizer<?> circuitBreakerMetrics(CircuitBreakerRegistry circuitBreakerRegistry) {
        return meterRegistry -> TaggedCircuitBreakerMetrics
                .ofCircuitBreakerRegistry(circuitBreakerRegistry)
                .bindTo(meterRegistry);
    }
}
