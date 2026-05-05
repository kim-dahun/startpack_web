package com.upmudoum.user.domain.auth.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class GatewayTokenResponseTest {

    @Test
    void tokenResponseDoesNotCarryRawTokenStringsForHttpOnlyCookieFlow() {
        Field[] fields = GatewayTokenResponse.class.getDeclaredFields();

        assertThat(fields)
                .extracting(Field::getName)
                .doesNotContain("accessToken", "refreshToken")
                .contains("accessTokenExpiresAt", "refreshTokenExpiresAt", "tokenDeliveryMethod");
    }
}
