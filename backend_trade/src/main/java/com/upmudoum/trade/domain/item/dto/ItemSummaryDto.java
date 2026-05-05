package com.upmudoum.trade.domain.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemSummaryDto {

    private ItemPriceDto quote;
    private ItemMetricsDto metrics;
}
