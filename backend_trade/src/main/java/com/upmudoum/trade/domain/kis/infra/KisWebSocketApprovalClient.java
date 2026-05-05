package com.upmudoum.trade.domain.kis.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.trade.domain.kis.dto.KisWebSocketApprovalRequest;
import com.upmudoum.trade.domain.kis.vo.KisProperties;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class KisWebSocketApprovalClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final KisProperties kisProperties;
    private final String approvalPath;

    public KisWebSocketApprovalClient(
            ObjectMapper objectMapper,
            KisProperties kisProperties,
            @Value("${trade.kis.websocket.approval-path:/oauth2/Approval}") String approvalPath
    ) {
        this.restClient = RestClient.create();
        this.objectMapper = objectMapper;
        this.kisProperties = kisProperties;
        this.approvalPath = approvalPath;
    }

    public String issueApprovalKey(KisTradeMode tradeMode) {
        kisProperties.validateCredentials();
        Object response = restClient.post()
                .uri(kisProperties.getBaseUrl(tradeMode) + approvalPath)
                .body(new KisWebSocketApprovalRequest(kisProperties.getAppKey(), kisProperties.getAppSecretKey()))
                .retrieve()
                .body(Object.class);
        return extractApprovalKey(response);
    }

    String extractApprovalKey(Object response) {
        JsonNode root = objectMapper.valueToTree(response);
        String approvalKey = firstText(root, "approval_key", "approvalKey");
        if (approvalKey == null || approvalKey.isBlank()) {
            throw new IllegalStateException("KIS approval response does not contain approval_key.");
        }
        return approvalKey;
    }

    private String firstText(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode field = node.path(fieldName);
            if (!field.isMissingNode() && !field.asText().isBlank()) {
                return field.asText();
            }
        }
        return null;
    }
}
