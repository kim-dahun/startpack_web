package com.upmudoum.trade.domain.trade.service;

import com.upmudoum.trade.domain.kis.infra.KisQueryFactory;
import com.upmudoum.trade.domain.kis.infra.KisRestClient;
import com.upmudoum.trade.domain.kis.vo.KisEndpoint;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import com.upmudoum.trade.domain.trade.dto.CreateDryRunTradeRequest;
import com.upmudoum.trade.domain.trade.dto.TradeHistoryDto;
import com.upmudoum.trade.domain.trade.entity.TradeHistory;
import com.upmudoum.trade.domain.trade.repository.TradeHistoryRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class TradeHistoryService {

    private final TradeHistoryRepository repository;
    private final KisRestClient kisRestClient;
    private final KisQueryFactory kisQueryFactory;

    public TradeHistoryService(TradeHistoryRepository repository, KisRestClient kisRestClient, KisQueryFactory kisQueryFactory) {
        this.repository = repository;
        this.kisRestClient = kisRestClient;
        this.kisQueryFactory = kisQueryFactory;
    }

    public List<TradeHistoryDto> findByAccountNo(String accountNo, KisTradeMode tradeMode) {
        kisRestClient.get(KisEndpoint.INQUIRE_DAILY_CCLD, kisQueryFactory.dailyCcld(accountNo, java.time.LocalDate.now()), tradeMode);
        return repository.findByAccountNoOrderByTradedAtDesc(accountNo).stream().map(this::toDto).toList();
    }

    public TradeHistoryDto createDryRun(CreateDryRunTradeRequest request) {
        return repository.findByIdempotencyKey(request.getIdempotencyKey())
                .map(this::toDto)
                .orElseGet(() -> toDto(repository.save(toEntity(request))));
    }

    private TradeHistory toEntity(CreateDryRunTradeRequest request) {
        TradeHistory history = new TradeHistory();
        history.setAccountNo(request.getAccountNo());
        history.setItemCode(request.getItemCode());
        history.setItemName(request.getItemName());
        history.setSide(request.getSide());
        history.setQuantity(request.getQuantity());
        history.setPrice(request.getPrice());
        history.setAmount(request.getPrice().multiply(java.math.BigDecimal.valueOf(request.getQuantity())));
        history.setIdempotencyKey(request.getIdempotencyKey());
        history.setTradedAt(Instant.now());
        return history;
    }

    private TradeHistoryDto toDto(TradeHistory history) {
        TradeHistoryDto dto = new TradeHistoryDto();
        dto.setId(history.getId());
        dto.setAccountNo(history.getAccountNo());
        dto.setItemCode(history.getItemCode());
        dto.setItemName(history.getItemName());
        dto.setSide(history.getSide());
        dto.setQuantity(history.getQuantity());
        dto.setPrice(history.getPrice());
        dto.setAmount(history.getAmount());
        dto.setIdempotencyKey(history.getIdempotencyKey());
        dto.setTradedAt(history.getTradedAt());
        return dto;
    }
}
