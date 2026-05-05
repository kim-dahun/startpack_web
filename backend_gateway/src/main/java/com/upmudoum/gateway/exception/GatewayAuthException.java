package com.upmudoum.gateway.exception;

public class GatewayAuthException extends RuntimeException {

    private final String code;

    public GatewayAuthException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
