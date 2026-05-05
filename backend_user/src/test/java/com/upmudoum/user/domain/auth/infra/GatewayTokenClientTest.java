package com.upmudoum.user.domain.auth.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.upmudoum.user.config.GatewayClientProperties;
import com.upmudoum.user.domain.auth.dto.LoginGroupResponse;
import com.upmudoum.user.domain.auth.vo.GatewayTokenIssueResult;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class GatewayTokenClientTest {

    @Test
    void tokenIssueRequestUsesVerifiedUserContextWithoutPassword() {
        RestClient.Builder restClientBuilder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        GatewayClientProperties properties = new GatewayClientProperties();
        properties.setBaseUrl(URI.create("http://localhost:9091"));
        properties.setAuthLoginPath("/api/auth/login");
        properties.setInternalGatewayId("backend_gateway");
        properties.setInternalGatewaySecret("local-dev-gateway-secret");
        GatewayTokenClient client = new GatewayTokenClient(properties, restClientBuilder);

        server.expect(once(), requestTo("http://localhost:9091/api/auth/login"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Internal-Gateway-Id", "backend_gateway"))
                .andExpect(header("X-Internal-Gateway-Secret", "local-dev-gateway-secret"))
                .andExpect(content().string(containsString("\"userId\":\"admin01\"")))
                .andExpect(content().string(containsString("\"loginId\":\"admin01\"")))
                .andExpect(content().string(containsString("\"serviceAccesses\":[\"ERP\"]")))
                .andExpect(content().string(containsString("\"roles\":[\"ADMIN\"]")))
                .andExpect(content().string(not(containsString("password"))))
                .andRespond(withSuccess("""
                        {"success":true,"data":{"userId":"admin01","loginId":"admin01","serviceId":"ERP","serviceAccesses":["ERP"],"roles":["ADMIN"],"tokenDeliveryMethod":"HTTP_ONLY_COOKIE"}}
                        """, MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.SET_COOKIE, "ACCESS_TOKEN=access; HttpOnly")
                        .header(HttpHeaders.SET_COOKIE, "REFRESH_TOKEN=refresh; HttpOnly"));

        GatewayTokenIssueResult result = client.issueUserToken(
                "COM001",
                "admin01",
                "admin01",
                "ERP",
                List.of("ERP"),
                List.of(new LoginGroupResponse("COM001", "ERP", "ADMIN", "ADMIN")),
                List.of("ADMIN")
        );

        assertThat(result.getToken().getUserId()).isEqualTo("admin01");
        assertThat(result.getSetCookieHeaders())
                .containsExactly("ACCESS_TOKEN=access; HttpOnly", "REFRESH_TOKEN=refresh; HttpOnly");
        server.verify();
    }
}
