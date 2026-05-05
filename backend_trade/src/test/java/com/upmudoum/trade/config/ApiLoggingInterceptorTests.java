package com.upmudoum.trade.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiLoggingInterceptorTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void apiCallPassesThroughLoggingInterceptor() throws Exception {
        mockMvc.perform(get("/api/trade/kis/call-logs")
                        .header("X-Request-Id", "log-test-request")
                        .header("X-User-Id", "log-test-user")
                        .header("X-Company-Id", "log-test-company"))
                .andExpect(status().isOk());
    }
}
