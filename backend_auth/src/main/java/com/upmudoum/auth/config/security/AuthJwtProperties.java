package com.upmudoum.auth.config.security;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "auth.jwt")
public class AuthJwtProperties {

    @NotBlank
    private String issuer;

    @NotNull
    private Duration clockSkew;

    @Valid
    @NotNull
    private TokenSpec access = new TokenSpec();

    @Valid
    @NotNull
    private TokenSpec refresh = new TokenSpec();

    @Valid
    @NotNull
    private TokenSpec openApi = new TokenSpec();

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Duration getClockSkew() {
        return clockSkew;
    }

    public void setClockSkew(Duration clockSkew) {
        this.clockSkew = clockSkew;
    }

    public TokenSpec getAccess() {
        return access;
    }

    public void setAccess(TokenSpec access) {
        this.access = access;
    }

    public TokenSpec getRefresh() {
        return refresh;
    }

    public void setRefresh(TokenSpec refresh) {
        this.refresh = refresh;
    }

    public TokenSpec getOpenApi() {
        return openApi;
    }

    public void setOpenApi(TokenSpec openApi) {
        this.openApi = openApi;
    }

    public static class TokenSpec {

        @NotBlank
        private String audience;

        @NotBlank
        private String secret;

        @NotNull
        private Duration ttl;

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public Duration getTtl() {
            return ttl;
        }

        public void setTtl(Duration ttl) {
            this.ttl = ttl;
        }
    }
}
