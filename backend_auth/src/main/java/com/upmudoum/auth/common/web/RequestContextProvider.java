package com.upmudoum.auth.common.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class RequestContextProvider {

    public String currentPath() {
        HttpServletRequest request = currentRequest();
        return request == null ? "N/A" : request.getRequestURI();
    }

    public String currentMethod() {
        HttpServletRequest request = currentRequest();
        return request == null ? "N/A" : request.getMethod();
    }

    public String currentClientIp() {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return "N/A";
        }

        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }
}
