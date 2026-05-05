package com.upmudoum.trade.domain.kis.infra;

import com.upmudoum.trade.domain.kis.service.KisApiCallLogService;
import com.upmudoum.trade.domain.kis.vo.KisEndpoint;
import com.upmudoum.trade.domain.kis.vo.KisProperties;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import java.net.URI;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@ConditionalOnProperty(name = "trade.kis.rest-client.enabled", havingValue = "true", matchIfMissing = true)
public class DefaultKisRestClient implements KisRestClient {

    private static final Logger log = LoggerFactory.getLogger(DefaultKisRestClient.class);
    private static final ParameterizedTypeReference<Map<String, Object>> MAP_RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestClient restClient;
    private final AuthOpenApiTokenClient tokenClient;
    private final KisProperties kisProperties;
    private final KisApiCallLogService logService;
    private final KisRateLimiter rateLimiter;

    public DefaultKisRestClient(
            AuthOpenApiTokenClient tokenClient,
            KisProperties kisProperties,
            KisApiCallLogService logService,
            KisRateLimiter rateLimiter
    ) {
        this.restClient = RestClient.create();
        this.tokenClient = tokenClient;
        this.kisProperties = kisProperties;
        this.logService = logService;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public Map<String, Object> get(String endpoint, Map<String, String> query, KisTradeMode tradeMode) {
        return get(resolveEndpoint(endpoint), query, tradeMode);
    }

    @Override
    public Map<String, Object> get(KisEndpoint endpoint, Map<String, String> query, KisTradeMode tradeMode) {
        long startedAt = System.currentTimeMillis();
        URI uri = buildUri(endpoint.getPath(), query, tradeMode);
        try {
            log.info("KIS_CALL_START method=GET endpoint={} trId={} tradeMode={} query={}", endpoint.getPath(), endpoint.transactionId(tradeMode), tradeMode, mask(query));
            String openApiToken = tokenClient.issueOpenApiToken(tradeMode);
            rateLimiter.acquire();
            Map<String, Object> response = restClient.get()
                    .uri(uri)
                    .header("authorization", "Bearer " + openApiToken)
                    .header("appkey", kisProperties.getAppKey())
                    .header("appsecret", kisProperties.getAppSecretKey())
                    .header("tr_id", endpoint.transactionId(tradeMode))
                    .header("custtype", "P")
                    .retrieve()
                    .body(MAP_RESPONSE_TYPE);
            long duration = System.currentTimeMillis() - startedAt;
            logService.log("GET", endpoint.getPath(), 200, duration, null, null);
            log.info("KIS_CALL_SUCCESS method=GET endpoint={} trId={} status=200 durationMs={}", endpoint.getPath(), endpoint.transactionId(tradeMode), duration);
            return response == null ? Map.of() : response;
        } catch (RestClientResponseException ex) {
            long duration = System.currentTimeMillis() - startedAt;
            logService.log("GET", endpoint.getPath(), ex.getStatusCode().value(), duration,
                    "KIS_HTTP_" + ex.getStatusCode().value(), ex.getResponseBodyAsString());
            log.warn("KIS_CALL_FAIL method=GET endpoint={} trId={} status={} durationMs={} body={}", endpoint.getPath(), endpoint.transactionId(tradeMode), ex.getStatusCode().value(), duration, ex.getResponseBodyAsString());
            throw ex;
        } catch (RuntimeException ex) {
            long duration = System.currentTimeMillis() - startedAt;
            logService.log("GET", endpoint.getPath(), 500, duration, "KIS_CLIENT_ERROR", ex.getMessage());
            log.error("KIS_CALL_ERROR method=GET endpoint={} trId={} durationMs={}", endpoint.getPath(), endpoint.transactionId(tradeMode), duration, ex);
            throw ex;
        }
    }

    @Override
    public Map<String, Object> post(KisEndpoint endpoint, Map<String, String> body, KisTradeMode tradeMode) {
        long startedAt = System.currentTimeMillis();
        URI uri = buildUri(endpoint.getPath(), Map.of(), tradeMode);
        try {
            log.info("KIS_CALL_START method=POST endpoint={} trId={} tradeMode={} body={}", endpoint.getPath(), endpoint.transactionId(tradeMode), tradeMode, mask(body));
            String openApiToken = tokenClient.issueOpenApiToken(tradeMode);
            rateLimiter.acquire();
            Map<String, Object> response = restClient.post()
                    .uri(uri)
                    .header("authorization", "Bearer " + openApiToken)
                    .header("appkey", kisProperties.getAppKey())
                    .header("appsecret", kisProperties.getAppSecretKey())
                    .header("tr_id", endpoint.transactionId(tradeMode))
                    .header("custtype", "P")
                    .body(body)
                    .retrieve()
                    .body(MAP_RESPONSE_TYPE);
            long duration = System.currentTimeMillis() - startedAt;
            logService.log("POST", endpoint.getPath(), 200, duration, null, null);
            log.info("KIS_CALL_SUCCESS method=POST endpoint={} trId={} status=200 durationMs={}", endpoint.getPath(), endpoint.transactionId(tradeMode), duration);
            return response == null ? Map.of() : response;
        } catch (RestClientResponseException ex) {
            long duration = System.currentTimeMillis() - startedAt;
            logService.log("POST", endpoint.getPath(), ex.getStatusCode().value(), duration,
                    "KIS_HTTP_" + ex.getStatusCode().value(), ex.getResponseBodyAsString());
            log.warn("KIS_CALL_FAIL method=POST endpoint={} trId={} status={} durationMs={} body={}", endpoint.getPath(), endpoint.transactionId(tradeMode), ex.getStatusCode().value(), duration, ex.getResponseBodyAsString());
            throw ex;
        } catch (RuntimeException ex) {
            long duration = System.currentTimeMillis() - startedAt;
            logService.log("POST", endpoint.getPath(), 500, duration, "KIS_CLIENT_ERROR", ex.getMessage());
            log.error("KIS_CALL_ERROR method=POST endpoint={} trId={} durationMs={}", endpoint.getPath(), endpoint.transactionId(tradeMode), duration, ex);
            throw ex;
        }
    }

    private URI buildUri(String endpoint, Map<String, String> query, KisTradeMode tradeMode) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(kisProperties.getBaseUrl(tradeMode) + endpoint);
        query.forEach(builder::queryParam);
        return builder.build(true).toUri();
    }

    private KisEndpoint resolveEndpoint(String path) {
        for (KisEndpoint endpoint : KisEndpoint.values()) {
            if (endpoint.getPath().equals(path)) {
                return endpoint;
            }
        }
        throw new IllegalArgumentException("unsupported KIS endpoint: " + path);
    }

    private Map<String, String> mask(Map<String, String> query) {
        return query.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getKey().toLowerCase().contains("secret") ? "***" : entry.getValue()
                ));
    }
}
