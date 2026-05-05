package com.upmudoum.groupware.common.infra;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class ApiAccessLogFilterTests {

    @Test
    void writesApiResultWithoutChangingResponse() throws Exception {
        ApiAccessLogFilter filter = new ApiAccessLogFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/groupware/notifications");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.setQueryString("status=UNREAD");
        request.addHeader("X-Request-Id", "request-1");
        request.addHeader("X-Com-Cd", "COM1");
        request.addHeader("X-User-Id", "user1");

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(filterChain.getRequest()).isSameAs(request);
    }
}
