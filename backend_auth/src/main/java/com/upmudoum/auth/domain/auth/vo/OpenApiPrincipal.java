package com.upmudoum.auth.domain.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OpenApiPrincipal {

    private final String subject;
    private final String clientId;
    private final List<String> scopes;

}
