package com.upmudoum.trade.domain.watchlist.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateWatchlistItemMetadataRequest {

    private Long groupId;
    private String memo;
    private List<String> tags;
}
