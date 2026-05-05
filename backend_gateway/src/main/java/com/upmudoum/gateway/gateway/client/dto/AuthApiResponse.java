package com.upmudoum.gateway.gateway.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthApiResponse<T> {

    private boolean success;
    private String code;
    private String message;
    private T data;
}
