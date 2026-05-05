package com.upmudoum.auth.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "auth.cookie")
public class AuthCookieProperties {

    private String accessTokenName = "ACCESS_TOKEN";
    private String refreshTokenName = "REFRESH_TOKEN";
    private boolean secure = false;
    private String sameSite = "Lax";
    private String path = "/";
}
