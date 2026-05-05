package com.upmudoum.gateway.exception;

public class AuthServerUnavailableException extends RuntimeException {

    public AuthServerUnavailableException(Throwable cause) {
        super("auth verification service is unavailable.", cause);
    }
}
