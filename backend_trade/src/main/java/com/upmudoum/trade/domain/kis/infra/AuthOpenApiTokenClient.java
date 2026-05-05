package com.upmudoum.trade.domain.kis.infra;

import com.upmudoum.trade.domain.kis.vo.KisTradeMode;

public interface AuthOpenApiTokenClient {

    String issueOpenApiToken(KisTradeMode tradeMode);
}
