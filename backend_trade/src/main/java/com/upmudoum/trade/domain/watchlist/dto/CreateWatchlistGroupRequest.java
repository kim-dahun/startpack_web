package com.upmudoum.trade.domain.watchlist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateWatchlistGroupRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String groupName;
}
