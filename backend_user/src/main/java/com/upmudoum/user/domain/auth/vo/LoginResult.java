package com.upmudoum.user.domain.auth.vo;

import com.upmudoum.user.domain.auth.dto.LoginInitResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResult {

    private LoginInitResponse response;
    private List<String> setCookieHeaders;
}
