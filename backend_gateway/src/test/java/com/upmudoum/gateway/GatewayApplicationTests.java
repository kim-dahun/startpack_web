package com.upmudoum.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
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
class GatewayApplicationTests {

    @Autowired
    private Environment environment;

    @Test
    void contextLoads() {
    }

    @Test
    void defaultServerPortIs9091() {
        assertThat(environment.getProperty("server.port")).isEqualTo("9091");
    }

}
