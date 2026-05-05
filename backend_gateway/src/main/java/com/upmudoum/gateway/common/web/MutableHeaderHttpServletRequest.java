package com.upmudoum.gateway.common.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MutableHeaderHttpServletRequest extends HttpServletRequestWrapper {

    private final Map<String, HeaderValue> customHeaders = new LinkedHashMap<>();

    public MutableHeaderHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    public void putHeader(String name, String value) {
        customHeaders.put(normalize(name), new HeaderValue(name, value));
    }

    @Override
    public String getHeader(String name) {
        HeaderValue customHeader = customHeaders.get(normalize(name));
        if (customHeader != null) {
            return customHeader.value();
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        HeaderValue customHeader = customHeaders.get(normalize(name));
        if (customHeader != null) {
            return Collections.enumeration(List.of(customHeader.value()));
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> names = new LinkedHashSet<>();
        Enumeration<String> headerNames = super.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            names.add(headerNames.nextElement());
        }
        customHeaders.values().forEach(header -> names.add(header.name()));
        return Collections.enumeration(new ArrayList<>(names));
    }

    private String normalize(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    private static class HeaderValue {

        private final String name;
        private final String value;

        HeaderValue(String name, String value) {
            this.name = name;
            this.value = value;
        }

        String name() {
            return name;
        }

        String value() {
            return value;
        }
    }
}
