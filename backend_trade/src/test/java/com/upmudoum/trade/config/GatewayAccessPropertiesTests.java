package com.upmudoum.trade.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GatewayAccessPropertiesTests {

    @Test
    void allowsOnlyConfiguredGatewayIdAndSecretWhenEnforced() {
        GatewayAccessProperties properties = enforcedProperties();

        assertThat(properties.isAllowed("backend_gateway", "shared-secret")).isTrue();
        assertThat(properties.isAllowed("backup_gateway", "backup-secret")).isTrue();
        assertThat(properties.isAllowed("frontend", "shared-secret")).isFalse();
        assertThat(properties.isAllowed("backend_gateway", "wrong-secret")).isFalse();
        assertThat(properties.isAllowed("backup_gateway", "shared-secret")).isFalse();
        assertThat(properties.isAllowed(null, "shared-secret")).isFalse();
    }

    @Test
    void allowsAllWhenEnforcementDisabledForTests() {
        GatewayAccessProperties properties = new GatewayAccessProperties();
        properties.setEnforce(false);

        assertThat(properties.isAllowed(null, null)).isTrue();
    }

    @Test
    void rejectsAllWhenSharedSecretIsBlankAndEnforced() {
        GatewayAccessProperties properties = new GatewayAccessProperties();
        properties.setEnforce(true);
        properties.setAllowedGateways(java.util.List.of(gateway("backend_gateway", "")));

        assertThat(properties.isAllowed("backend_gateway", "")).isFalse();
    }

    private GatewayAccessProperties enforcedProperties() {
        GatewayAccessProperties properties = new GatewayAccessProperties();
        properties.setEnforce(true);
        properties.setAllowedGateways(java.util.List.of(
                gateway("backend_gateway", "shared-secret"),
                gateway("backup_gateway", "backup-secret")
        ));
        return properties;
    }

    private GatewayAccessProperties.AllowedGateway gateway(String gatewayId, String sharedSecret) {
        GatewayAccessProperties.AllowedGateway gateway = new GatewayAccessProperties.AllowedGateway();
        gateway.setGatewayId(gatewayId);
        gateway.setSharedSecret(sharedSecret);
        return gateway;
    }
}
