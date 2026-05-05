package com.upmudoum.trade.domain.kis.infra;

import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import java.util.Map;

public interface KisRestClient {

    Map<String, Object> get(String endpoint, Map<String, String> query, KisTradeMode tradeMode);

    Map<String, Object> post(com.upmudoum.trade.domain.kis.vo.KisEndpoint endpoint, Map<String, String> body, KisTradeMode tradeMode);

    default Map<String, Object> get(com.upmudoum.trade.domain.kis.vo.KisEndpoint endpoint, Map<String, String> query, KisTradeMode tradeMode) {
        return get(endpoint.getPath(), query, tradeMode);
    }
}
