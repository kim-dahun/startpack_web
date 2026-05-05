package com.upmudoum.erp.common.gateway;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.web.servlet.HandlerInterceptor;

public class GatewayOnlyInterceptor implements HandlerInterceptor {

    public static final String GATEWAY_ID_HEADER = "X-Internal-Gateway-Id";
    public static final String GATEWAY_SECRET_HEADER = "X-Internal-Gateway-Secret";

    private final boolean gatewayOnly;
    private final Map<String, String> allowedGatewayCredentials;

    public GatewayOnlyInterceptor(boolean gatewayOnly, Map<String, String> allowedGatewayCredentials) {
        this.gatewayOnly = gatewayOnly;
        this.allowedGatewayCredentials = allowedGatewayCredentials;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!gatewayOnly) {
            return true;
        }
        String gatewayId = request.getHeader(GATEWAY_ID_HEADER);
        String gatewaySecret = request.getHeader(GATEWAY_SECRET_HEADER);
        if (gatewaySecret != null && gatewaySecret.equals(allowedGatewayCredentials.get(gatewayId))) {
            return true;
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "ERP API must be called through gateway");
        return false;
    }
}
