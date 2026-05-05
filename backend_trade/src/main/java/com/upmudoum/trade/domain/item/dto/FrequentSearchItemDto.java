package com.upmudoum.trade.domain.item.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FrequentSearchItemDto {

    private Long id;
    private String userId;
    private String itemCode;
    private String itemName;
    private String marketCode;
    private long searchCount;
    private Instant lastSearchedAt;
}
