package com.upmudoum.trade.domain.watchlist.dto;

import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WatchlistItemDto {

    private Long id;
    private String userId;
    private String itemCode;
    private String itemName;
    private Long groupId;
    private String memo;
    private List<String> tags;
    private Instant createdAt;
}
