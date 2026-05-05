package com.upmudoum.trade.domain.watchlist.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WatchlistGroupDto {

    private Long id;
    private String userId;
    private String groupName;
    private Instant createdAt;
}
