package com.upmudoum.auth.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ErrorResponseData {

    private final Instant timestamp;
    private final String path;
}
