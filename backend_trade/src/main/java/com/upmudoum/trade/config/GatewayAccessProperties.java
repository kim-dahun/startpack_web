package com.upmudoum.trade.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "trade.gateway")
public class GatewayAccessProperties {

    private String gatewayIdHeaderName = "X-Internal-Gateway-Id";
    private String gatewaySecretHeaderName = "X-Internal-Gateway-Secret";
    private List<AllowedGateway> allowedGateways = new ArrayList<>();
    private boolean enforce = true;

    public boolean isAllowed(String gatewayId, String gatewaySecret) {
        if (!enforce) {
            return true;
        }
        if (gatewayId == null || gatewaySecret == null) {
            return false;
        }
        return allowedGateways.stream()
                .anyMatch(allowedGateway -> allowedGateway.matches(gatewayId, gatewaySecret));
    }

    @Getter
    @Setter
    public static class AllowedGateway {

        private String gatewayId;
        private String sharedSecret;

        public boolean matches(String requestedGatewayId, String requestedSecret) {
            if (gatewayId == null || gatewayId.isBlank() || sharedSecret == null || sharedSecret.isBlank()) {
                return false;
            }
            return gatewayId.equals(requestedGatewayId) && sharedSecret.equals(requestedSecret);
        }
    }
}
