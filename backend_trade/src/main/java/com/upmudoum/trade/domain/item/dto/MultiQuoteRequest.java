package com.upmudoum.trade.domain.item.dto;

import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MultiQuoteRequest {

    private List<String> itemCodes;
    private KisTradeMode tradeMode = KisTradeMode.LIVE;
}
