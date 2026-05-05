package com.upmudoum.auth.config.security;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "auth.gateway")
@Getter
@Setter
public class AuthGatewayAccessProperties {

    private boolean enforce = true;

    @NotBlank
    private String gatewayIdHeaderName = "X-Internal-Gateway-Id";

    @NotBlank
    private String gatewaySecretHeaderName = "X-Internal-Gateway-Secret";

    @Valid
    private List<AllowedGateway> allowedGateways = new ArrayList<>();

    public boolean isAllowed(String gatewayId, String sharedSecret) {
        if (!enforce) {
            return true;
        }
        return allowedGateways.stream()
                .anyMatch(gateway -> gateway.matches(gatewayId, sharedSecret));
    }

    @Getter
    @Setter
    public static class AllowedGateway {

        @NotBlank
        private String gatewayId;

        @NotBlank
        private String sharedSecret;

        public boolean matches(String gatewayId, String sharedSecret) {
            return this.gatewayId.equals(gatewayId) && this.sharedSecret.equals(sharedSecret);
        }
    }
}
