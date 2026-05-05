package com.upmudoum.trade.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TradeWebMvcConfig implements WebMvcConfigurer {

    private final GatewayOnlyInterceptor gatewayOnlyInterceptor;
    private final ApiLoggingInterceptor apiLoggingInterceptor;

    public TradeWebMvcConfig(GatewayOnlyInterceptor gatewayOnlyInterceptor, ApiLoggingInterceptor apiLoggingInterceptor) {
        this.gatewayOnlyInterceptor = gatewayOnlyInterceptor;
        this.apiLoggingInterceptor = apiLoggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(gatewayOnlyInterceptor)
                .order(0)
                .addPathPatterns("/api/**");
        registry.addInterceptor(apiLoggingInterceptor)
                .order(1)
                .addPathPatterns("/api/**");
    }
}
