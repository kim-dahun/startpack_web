package com.upmudoum.trade.domain.trade.service;

import com.upmudoum.trade.domain.kis.infra.KisQueryFactory;
import com.upmudoum.trade.domain.kis.infra.KisResponseExtractor;
import com.upmudoum.trade.domain.kis.infra.KisRestClient;
import com.upmudoum.trade.domain.kis.vo.KisEndpoint;
import com.upmudoum.trade.domain.trade.dto.OrderableAmountDto;
import com.upmudoum.trade.domain.trade.dto.OrderableAmountRequest;
import com.upmudoum.trade.domain.trade.dto.PlaceOrderRequest;
import com.upmudoum.trade.domain.trade.dto.PlaceOrderResultDto;
import com.upmudoum.trade.domain.trade.vo.TradeSide;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class KisOrderService {

    private final KisRestClient kisRestClient;
    private final KisQueryFactory kisQueryFactory;

    public KisOrderService(KisRestClient kisRestClient, KisQueryFactory kisQueryFactory) {
        this.kisRestClient = kisRestClient;
        this.kisQueryFactory = kisQueryFactory;
    }

    public OrderableAmountDto orderableAmount(OrderableAmountRequest request) {
        Map<String, Object> response = kisRestClient.get(
                KisEndpoint.INQUIRE_PSBL_ORDER,
                kisQueryFactory.orderableAmount(request.getAccountNo(), request.getItemCode(), request.getPrice().toPlainString()),
                request.getTradeMode()
        );
        Map<String, Object> output = KisResponseExtractor.object(response, "output");
        OrderableAmountDto dto = new OrderableAmountDto();
        dto.setAccountNo(request.getAccountNo());
        dto.setItemCode(request.getItemCode());
        dto.setPrice(request.getPrice());
        dto.setTradeMode(request.getTradeMode());
        dto.setOrderableCashAmount(KisResponseExtractor.decimal(output, "ord_psbl_cash", "nrcvb_buy_amt", "psbl_amt"));
        dto.setOrderableQuantity(KisResponseExtractor.decimal(output, "ord_psbl_qty", "max_buy_qty", "psbl_qty").longValue());
        dto.setRaw(response);
        return dto;
    }

    public PlaceOrderResultDto placeOrder(PlaceOrderRequest request) {
        KisEndpoint endpoint = request.getSide() == TradeSide.BUY ? KisEndpoint.ORDER_CASH_BUY : KisEndpoint.ORDER_CASH_SELL;
        Map<String, Object> response = kisRestClient.post(
                endpoint,
                kisQueryFactory.cashOrder(request.getAccountNo(), request.getItemCode(), request.getQuantity(), request.getPrice().toPlainString()),
                request.getTradeMode()
        );
        Map<String, Object> output = KisResponseExtractor.object(response, "output");
        PlaceOrderResultDto dto = new PlaceOrderResultDto();
        dto.setAccountNo(request.getAccountNo());
        dto.setItemCode(request.getItemCode());
        dto.setSide(request.getSide());
        dto.setQuantity(request.getQuantity());
        dto.setPrice(request.getPrice());
        dto.setTradeMode(request.getTradeMode());
        dto.setOrderNo(KisResponseExtractor.text(output, "ODNO", "odno", "orderNo"));
        dto.setBranchNo(KisResponseExtractor.text(output, "KRX_FWDG_ORD_ORGNO", "ord_gno_brno", "branchNo"));
        dto.setResponseCode(KisResponseExtractor.text(response, "rt_cd", "code"));
        dto.setMessage(KisResponseExtractor.text(response, "msg1", "message"));
        dto.setRaw(response);
        return dto;
    }
}
