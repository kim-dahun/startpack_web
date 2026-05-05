package com.upmudoum.trade.domain.trade.service;

import com.upmudoum.trade.domain.account.entity.AccountPositionSnapshot;
import com.upmudoum.trade.domain.account.repository.AccountPositionSnapshotRepository;
import com.upmudoum.trade.domain.account.repository.AccountSnapshotRepository;
import com.upmudoum.trade.domain.trade.dto.OrderValidationRequest;
import com.upmudoum.trade.domain.trade.dto.OrderValidationResultDto;
import com.upmudoum.trade.domain.trade.vo.TradeSide;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderValidationService {

    private final AccountSnapshotRepository accountSnapshotRepository;
    private final AccountPositionSnapshotRepository accountPositionSnapshotRepository;

    public OrderValidationService(
            AccountSnapshotRepository accountSnapshotRepository,
            AccountPositionSnapshotRepository accountPositionSnapshotRepository
    ) {
        this.accountSnapshotRepository = accountSnapshotRepository;
        this.accountPositionSnapshotRepository = accountPositionSnapshotRepository;
    }

    @Transactional(readOnly = true)
    public OrderValidationResultDto validate(OrderValidationRequest request) {
        BigDecimal requiredAmount = request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        BigDecimal availableCashAmount = availableCashAmount(request);

        OrderValidationResultDto result = new OrderValidationResultDto();
        result.setAccountNo(request.getAccountNo());
        result.setItemCode(request.getItemCode());
        result.setSide(request.getSide());
        result.setRequestedQuantity(request.getQuantity());
        result.setPrice(request.getPrice());
        result.setRequiredAmount(requiredAmount);
        result.setAvailableCashAmount(availableCashAmount);
        result.setAvailableQuantity(availableQuantity(request));

        if (request.getSide() == TradeSide.BUY) {
            validateBuy(result, requiredAmount, availableCashAmount);
        } else {
            validateSell(result, request.getQuantity(), result.getAvailableQuantity());
        }
        return result;
    }

    private BigDecimal availableCashAmount(OrderValidationRequest request) {
        if (request.getAvailableCashAmount() != null) {
            return request.getAvailableCashAmount();
        }
        return accountSnapshotRepository.findTopByAccountNoOrderByCapturedAtDesc(request.getAccountNo())
                .map(account -> account.getCashAmount())
                .orElse(BigDecimal.ZERO);
    }

    private Long availableQuantity(OrderValidationRequest request) {
        if (request.getSide() != TradeSide.SELL) {
            return request.getAvailableQuantity();
        }
        return accountPositionSnapshotRepository.findTopByAccountNoAndItemCodeOrderByCapturedAtDesc(request.getAccountNo(), request.getItemCode())
                .map(AccountPositionSnapshot::getOrderableQuantity)
                .orElse(null);
    }

    private void validateBuy(OrderValidationResultDto result, BigDecimal requiredAmount, BigDecimal availableCashAmount) {
        if (availableCashAmount.compareTo(requiredAmount) >= 0) {
            result.setAllowed(true);
            return;
        }
        result.setAllowed(false);
        result.setFailureReason("insufficient cash amount");
    }

    private void validateSell(OrderValidationResultDto result, long requestedQuantity, Long availableQuantity) {
        if (availableQuantity == null) {
            result.setAllowed(false);
            result.setFailureReason("position snapshot is required for sell validation");
            return;
        }
        if (availableQuantity >= requestedQuantity) {
            result.setAllowed(true);
            return;
        }
        result.setAllowed(false);
        result.setFailureReason("insufficient available quantity");
    }
}
