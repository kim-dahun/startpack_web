package com.upmudoum.gateway.gateway.filter;

import com.upmudoum.gateway.gateway.client.AuthVerificationClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = {
        "gateway.routes.services.auth-url=http://localhost:18081",
        "gateway.routes.services.user-url=http://localhost:18082",
        "gateway.routes.services.erp-url=http://localhost:18083",
        "gateway.routes.services.groupware-url=http://localhost:18084",
        "gateway.routes.services.trade-url=http://localhost:18085",
        "gateway.auth.verify-path=/api/v1/auth/tokens/verify",
        "gateway.auth.timeout=3s",
        "gateway.cors.allowed-origins=http://localhost:3000,http://localhost:5176,http://localhost:5177,http://localhost:5178",
        "gateway.cors.allowed-headers=Authorization,Content-Type,Accept,Origin,X-Requested-With,X-Request-Id,X-Open-Api-Token,X-User-Id,X-Com-Cd,X-Roles,X-Token-Type"
})
class AuthenticationFilterTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthVerificationClient authVerificationClient;

    @Test
    void publicHealthRouteDoesNotRequireToken() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void authenticatedRouteRequiresToken() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("TOKEN_REQUIRED"));
    }

    @Test
    void allowedOriginPreflightPasses() throws Exception {
        mockMvc.perform(options("/api/users/me")
                        .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:3000"));
    }

    @Test
    void localhost5176PreflightPassesWithCredentialsMethodsAndHeaders() throws Exception {
        mockMvc.perform(options("/api/users/me")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5176")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.POST.name())
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
                                "authorization,content-type,x-request-id,x-requested-with"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5176"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                        org.hamcrest.Matchers.containsString(HttpMethod.POST.name())))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                        org.hamcrest.Matchers.containsString("authorization")));
    }

    @Test
    void localhost5177PreflightPassesWithCredentialsMethodsAndHeaders() throws Exception {
        mockMvc.perform(options("/api/erp/status")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5177")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.POST.name())
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
                                "authorization,content-type,x-request-id,x-requested-with"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5177"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                        org.hamcrest.Matchers.containsString(HttpMethod.POST.name())))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                        org.hamcrest.Matchers.containsString("authorization")));
    }

    @Test
    void localhost5178PreflightPassesWithCredentialsMethodsAndHeaders() throws Exception {
        mockMvc.perform(options("/api/groupware/status")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5178")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.POST.name())
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
                                "authorization,content-type,x-request-id,x-requested-with"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5178"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                        org.hamcrest.Matchers.containsString(HttpMethod.POST.name())))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                        org.hamcrest.Matchers.containsString("authorization")));
    }

    @Test
    void apiRequestFromLocalhost5176PassesCorsLayerAndReturnsAuthResponse() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5176"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5176"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
                .andExpect(jsonPath("$.code").value("TOKEN_REQUIRED"));
    }

    @Test
    void apiRequestFromLocalhost5177PassesCorsLayerAndReturnsAuthResponse() throws Exception {
        mockMvc.perform(get("/api/erp/status")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5177"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5177"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
                .andExpect(jsonPath("$.code").value("TOKEN_REQUIRED"));
    }

    @Test
    void apiRequestFromLocalhost5178PassesCorsLayerAndReturnsAuthResponse() throws Exception {
        mockMvc.perform(get("/api/groupware/status")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5178"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5178"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
                .andExpect(jsonPath("$.code").value("TOKEN_REQUIRED"));
    }

    @Test
    void disallowedOriginPreflightIsRejected() throws Exception {
        mockMvc.perform(options("/api/users/me")
                        .header(HttpHeaders.ORIGIN, "http://evil.example")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name()))
                .andExpect(status().isForbidden());
    }
}
