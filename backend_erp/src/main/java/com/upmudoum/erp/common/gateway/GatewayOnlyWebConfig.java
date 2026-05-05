package com.upmudoum.erp.common.gateway;

import com.upmudoum.erp.common.logging.ApiAccessLoggingInterceptor;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GatewayOnlyWebConfig implements WebMvcConfigurer {

    private final boolean gatewayOnly;
    private final Map<String, String> allowedGatewayCredentials;

    public GatewayOnlyWebConfig(@Value("${erp.gateway.only:true}") boolean gatewayOnly,
                                @Value("${erp.gateway.allowed-credentials:backend_gateway:local-dev-gateway-secret}") String allowedCredentials) {
        this.gatewayOnly = gatewayOnly;
        this.allowedGatewayCredentials = parseCredentials(allowedCredentials);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiAccessLoggingInterceptor())
                .addPathPatterns("/api/**", "/actuator/**");
        registry.addInterceptor(new GatewayOnlyInterceptor(gatewayOnly, allowedGatewayCredentials))
                .addPathPatterns("/api/**")
                .excludePathPatterns("/actuator/**");
    }

    private Map<String, String> parseCredentials(String allowedCredentials) {
        return Arrays.stream(allowedCredentials.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(value -> value.split(":", 2))
                .filter(parts -> parts.length == 2 && !parts[0].isBlank() && !parts[1].isBlank())
                .collect(Collectors.toUnmodifiableMap(parts -> parts[0], parts -> parts[1]));
    }
}
