package com.upmudoum.user.config;

import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway.client")
public class GatewayClientProperties {

    private URI baseUrl = URI.create("http://localhost:9091");
    private String authLoginPath = "/api/auth/login";
    private String internalGatewayId = "backend_gateway";
    private String internalGatewaySecret = "local-dev-gateway-secret";

    public URI getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URI baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAuthLoginPath() {
        return authLoginPath;
    }

    public void setAuthLoginPath(String authLoginPath) {
        this.authLoginPath = authLoginPath;
    }

    public String getInternalGatewayId() {
        return internalGatewayId;
    }

    public void setInternalGatewayId(String internalGatewayId) {
        this.internalGatewayId = internalGatewayId;
    }

    public String getInternalGatewaySecret() {
        return internalGatewaySecret;
    }

    public void setInternalGatewaySecret(String internalGatewaySecret) {
        this.internalGatewaySecret = internalGatewaySecret;
    }
}
