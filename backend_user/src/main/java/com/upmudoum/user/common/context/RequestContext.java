package com.upmudoum.user.common.context;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestContext {

    @NotBlank
    private String comCd;

    @NotBlank
    private String userId;
}
