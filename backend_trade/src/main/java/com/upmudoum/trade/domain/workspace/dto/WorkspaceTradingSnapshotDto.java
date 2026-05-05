package com.upmudoum.trade.domain.workspace.dto;

import com.upmudoum.trade.domain.account.dto.AccountBalanceDetailDto;
import com.upmudoum.trade.domain.account.dto.PositionDto;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import com.upmudoum.trade.domain.trade.dto.OrderableAmountDto;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WorkspaceTradingSnapshotDto {

    private String itemCode;
    private String accountNo;
    private KisTradeMode tradeMode;
    private Instant capturedAt;
    private AccountBalanceDetailDto balance;
    private List<PositionDto> positions;
    private PositionDto currentPosition;
    private OrderableAmountDto orderableAmount;
}
