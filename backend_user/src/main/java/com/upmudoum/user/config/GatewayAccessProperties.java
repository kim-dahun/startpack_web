package com.upmudoum.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "user.gateway-access")
public class GatewayAccessProperties {

    private boolean enabled = true;
    private String gatewayIdHeaderName = "X-Internal-Gateway-Id";
    private String gatewaySecretHeaderName = "X-Internal-Gateway-Secret";
    private String allowedGateways = "backend_gateway:local-dev-gateway-secret";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getGatewayIdHeaderName() {
        return gatewayIdHeaderName;
    }

    public void setGatewayIdHeaderName(String gatewayIdHeaderName) {
        this.gatewayIdHeaderName = gatewayIdHeaderName;
    }

    public String getGatewaySecretHeaderName() {
        return gatewaySecretHeaderName;
    }

    public void setGatewaySecretHeaderName(String gatewaySecretHeaderName) {
        this.gatewaySecretHeaderName = gatewaySecretHeaderName;
    }

    public String getAllowedGateways() {
        return allowedGateways;
    }

    public void setAllowedGateways(String allowedGateways) {
        this.allowedGateways = allowedGateways;
    }
}
