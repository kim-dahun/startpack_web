package com.upmudoum.trade.domain.kis.service;

import com.upmudoum.trade.domain.kis.dto.KisApiCallLogDto;
import com.upmudoum.trade.domain.kis.entity.KisApiCallLog;
import com.upmudoum.trade.domain.kis.repository.KisApiCallLogRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class KisApiCallLogService {

    private final KisApiCallLogRepository repository;

    public KisApiCallLogService(KisApiCallLogRepository repository) {
        this.repository = repository;
    }

    public void log(String method, String endpoint, int statusCode, long elapsedMillis, String errorCode, String errorMessage) {
        KisApiCallLog log = new KisApiCallLog();
        log.setMethod(method);
        log.setEndpoint(endpoint);
        log.setStatusCode(statusCode);
        log.setElapsedMillis(elapsedMillis);
        log.setErrorCode(errorCode);
        log.setErrorMessage(errorMessage);
        log.setCalledAt(Instant.now());
        repository.save(log);
    }

    public List<KisApiCallLogDto> findAll() {
        return repository.findTop50ByOrderByCalledAtDesc().stream().map(this::toDto).toList();
    }

    private KisApiCallLogDto toDto(KisApiCallLog log) {
        KisApiCallLogDto dto = new KisApiCallLogDto();
        dto.setId(log.getId());
        dto.setMethod(log.getMethod());
        dto.setEndpoint(log.getEndpoint());
        dto.setStatusCode(log.getStatusCode());
        dto.setElapsedMillis(log.getElapsedMillis());
        dto.setErrorCode(log.getErrorCode());
        dto.setErrorMessage(log.getErrorMessage());
        dto.setCalledAt(log.getCalledAt());
        return dto;
    }
}
