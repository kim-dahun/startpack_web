package com.upmudoum.trade.domain.master.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.trade.domain.master.dto.TradeMasterDownloadImportRequest;
import com.upmudoum.trade.domain.master.dto.TradeMasterImportHistoryDto;
import com.upmudoum.trade.domain.master.dto.TradeMasterImportJobDto;
import com.upmudoum.trade.domain.master.dto.TradeMasterImportRequest;
import com.upmudoum.trade.domain.master.dto.TradeMasterImportResultDto;
import com.upmudoum.trade.domain.master.dto.TradeMasterImportRowDto;
import com.upmudoum.trade.domain.master.dto.TradeMasterStatusDto;
import com.upmudoum.trade.domain.master.dto.TradeMasterTypeDto;
import com.upmudoum.trade.domain.master.entity.TradeMasterImportHistory;
import com.upmudoum.trade.domain.master.entity.TradeMasterImportLock;
import com.upmudoum.trade.domain.master.infra.TradeMasterParser;
import com.upmudoum.trade.domain.master.infra.TradeMasterSourceDownloader;
import com.upmudoum.trade.domain.item.repository.ItemMasterRepository;
import com.upmudoum.trade.domain.master.querydsl.TradeMasterImportHistoryQueryRepository;
import com.upmudoum.trade.domain.master.repository.TradeMasterImportHistoryRepository;
import com.upmudoum.trade.domain.master.repository.TradeMasterImportLockRepository;
import com.upmudoum.trade.domain.master.vo.TradeMasterImportStatus;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TradeMasterImportService {

    private static final Duration DOWNLOAD_REQUEST_INTERVAL = Duration.ofMinutes(1);

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final TradeMasterImportHistoryRepository historyRepository;
    private final TradeMasterImportLockRepository lockRepository;
    private final TradeMasterImportHistoryQueryRepository historyQueryRepository;
    private final TradeMasterSourceDownloader sourceDownloader;
    private final TradeMasterParser masterParser;
    private final TradeMasterSourceRegistry sourceRegistry;
    private final ItemMasterRepository itemMasterRepository;
    private final TransactionTemplate transactionTemplate;
    private final TaskExecutor masterImportTaskExecutor;
    private final String itemMasterTable;

    public TradeMasterImportService(
            JdbcTemplate jdbcTemplate,
            ObjectMapper objectMapper,
            TradeMasterImportHistoryRepository historyRepository,
            TradeMasterImportLockRepository lockRepository,
            TradeMasterImportHistoryQueryRepository historyQueryRepository,
            TradeMasterSourceDownloader sourceDownloader,
            TradeMasterParser masterParser,
            TradeMasterSourceRegistry sourceRegistry,
            ItemMasterRepository itemMasterRepository,
            TransactionTemplate transactionTemplate,
            TaskExecutor masterImportTaskExecutor,
            @Value("${spring.jpa.properties.hibernate.default_schema:}") String defaultSchema
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.historyRepository = historyRepository;
        this.lockRepository = lockRepository;
        this.historyQueryRepository = historyQueryRepository;
        this.sourceDownloader = sourceDownloader;
        this.masterParser = masterParser;
        this.sourceRegistry = sourceRegistry;
        this.itemMasterRepository = itemMasterRepository;
        this.transactionTemplate = transactionTemplate;
        this.masterImportTaskExecutor = masterImportTaskExecutor;
        this.itemMasterTable = tableName(defaultSchema, "item_master");
    }

    public TradeMasterImportResultDto downloadAndImport(TradeMasterDownloadImportRequest request) {
        TradeMasterImportHistory history = createRunningHistory(request);
        try {
            runDownloadAndImport(history.getId(), request);
            return historyRepository.findById(history.getId())
                    .map(this::toResult)
                    .orElseThrow(() -> new IllegalStateException("master import history not found: " + history.getId()));
        } catch (RuntimeException ex) {
            throw ex;
        }
    }

    public TradeMasterImportJobDto downloadAndImportAsync(TradeMasterDownloadImportRequest request) {
        TradeMasterImportHistory history = createRunningHistory(request);
        masterImportTaskExecutor.execute(() -> runDownloadAndImport(history.getId(), request));
        return toJob(history);
    }

    public List<TradeMasterImportResultDto> downloadAndImportDefaults() {
        return sourceRegistry.defaultImportTargets().stream()
                .map(masterType -> {
                    TradeMasterDownloadImportRequest request = new TradeMasterDownloadImportRequest();
                    request.setMasterType(masterType);
                    request.setSourceVersion(java.time.LocalDate.now().toString());
                    return downloadAndImport(request);
                })
                .toList();
    }

    public List<TradeMasterImportJobDto> downloadAndImportDefaultsAsync() {
        return sourceRegistry.defaultImportTargets().stream()
                .map(masterType -> {
                    TradeMasterDownloadImportRequest request = new TradeMasterDownloadImportRequest();
                    request.setMasterType(masterType);
                    request.setSourceVersion(java.time.LocalDate.now().toString());
                    return downloadAndImportAsync(request);
                })
                .toList();
    }

    public TradeMasterImportResultDto importMaster(TradeMasterImportRequest request) {
        Instant startedAt = Instant.now();
        try {
            return transactionTemplate.execute(status -> {
                TradeMasterImportHistory history = new TradeMasterImportHistory();
                history.setMasterType(request.getMasterType());
                history.setSourceFileName(request.getSourceFileName());
                history.setSourceVersion(request.getSourceVersion());
                history.setStartedAt(startedAt);
                replaceMasterRows(request, startedAt);
                history.setImportedCount(request.getRows().size());
                history.setSuccess(true);
                history.setImportStatus(TradeMasterImportStatus.SUCCESS);
                history.setFinishedAt(Instant.now());
                historyRepository.save(history);
                return toResult(history);
            });
        } catch (RuntimeException ex) {
            saveFailedHistory(request.getMasterType(), request.getSourceFileName(), request.getSourceVersion(), startedAt, ex);
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public List<TradeMasterImportHistoryDto> histories(TradeMasterType masterType) {
        return historyQueryRepository.findHistories(masterType, 50);
    }

    @Transactional(readOnly = true)
    public List<TradeMasterStatusDto> statuses() {
        return sourceRegistry.definitions().stream()
                .map(definition -> definition.getMasterType())
                .map(this::status)
                .toList();
    }

    public List<TradeMasterTypeDto> types() {
        return sourceRegistry.typeDtos();
    }

    public TradeMasterTypeDto type(TradeMasterType masterType) {
        return sourceRegistry.getDto(masterType);
    }

    private TradeMasterStatusDto status(TradeMasterType masterType) {
        TradeMasterStatusDto dto = new TradeMasterStatusDto();
        dto.setMasterType(masterType);
        dto.setItemCount(itemMasterRepository.countByMasterType(masterType));
        historyRepository.findTopByMasterTypeOrderByStartedAtDesc(masterType).ifPresent(history -> {
            dto.setLastImportedAt(history.getFinishedAt());
            dto.setLastSourceFileName(history.getSourceFileName());
            dto.setLastSourceVersion(history.getSourceVersion());
            dto.setLastImportStatus(statusOf(history));
            dto.setLastImportSuccess(history.isSuccess());
        });
        return dto;
    }

    private TradeMasterImportHistory createRunningHistory(TradeMasterDownloadImportRequest request) {
        return transactionTemplate.execute(status -> {
            TradeMasterImportLock lock = lockForUpdate(request.getMasterType());
            validateDownloadRequest(lock);
            Instant now = Instant.now();
            TradeMasterImportHistory history = new TradeMasterImportHistory();
            history.setMasterType(request.getMasterType());
            history.setSourceFileName(sourceLabel(request));
            history.setSourceVersion(request.getSourceVersion());
            history.setImportStatus(TradeMasterImportStatus.RUNNING);
            history.setImportedCount(0);
            history.setStartedAt(now);
            history.setFinishedAt(now);
            history.setSuccess(false);
            history.setFailureReason("RUNNING");
            TradeMasterImportHistory savedHistory = historyRepository.save(history);
            lock.setImportStatus(TradeMasterImportStatus.RUNNING);
            lock.setHistoryId(savedHistory.getId());
            lock.setLastRequestedAt(now);
            lock.setUpdatedAt(now);
            return savedHistory;
        });
    }

    private TradeMasterImportLock lockForUpdate(TradeMasterType masterType) {
        return lockRepository.findByMasterTypeForUpdate(masterType)
                .orElseGet(() -> {
                    lockRepository.saveAndFlush(newLock(masterType));
                    return lockRepository.findByMasterTypeForUpdate(masterType)
                            .orElseThrow(() -> new IllegalStateException("master import lock not found: " + masterType));
                });
    }

    private TradeMasterImportLock newLock(TradeMasterType masterType) {
        TradeMasterImportLock lock = new TradeMasterImportLock();
        lock.setMasterType(masterType);
        lock.setImportStatus(TradeMasterImportStatus.SUCCESS);
        lock.setLastRequestedAt(Instant.EPOCH);
        lock.setUpdatedAt(Instant.EPOCH);
        return lock;
    }

    private void validateDownloadRequest(TradeMasterImportLock lock) {
        if (lock.getImportStatus() == TradeMasterImportStatus.RUNNING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "master download import is already running for " + lock.getMasterType()
            );
        }
        Instant threshold = Instant.now().minus(DOWNLOAD_REQUEST_INTERVAL);
        if (lock.getLastRequestedAt() != null && lock.getLastRequestedAt().isAfter(threshold)) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "master download import can be requested once per minute for " + lock.getMasterType()
            );
        }
    }

    private void runDownloadAndImport(Long historyId, TradeMasterDownloadImportRequest request) {
        try {
            List<TradeMasterSourceDownloader.DownloadedMasterSource> sources = sourceDownloader.downloadAll(request.getMasterType(), request.getSourceUrl());
            TradeMasterImportRequest importRequest = new TradeMasterImportRequest();
            importRequest.setMasterType(request.getMasterType());
            importRequest.setSourceFileName(sourceFileName(sources));
            importRequest.setSourceVersion(request.getSourceVersion());
            importRequest.setRows(parseSources(request.getMasterType(), sources));
            transactionTemplate.executeWithoutResult(status -> {
                TradeMasterImportHistory history = historyRepository.findById(historyId)
                        .orElseThrow(() -> new IllegalStateException("master import history not found: " + historyId));
                history.setSourceFileName(importRequest.getSourceFileName());
                replaceMasterRows(importRequest, history.getStartedAt());
                history.setImportedCount(importRequest.getRows().size());
                history.setSuccess(true);
                history.setImportStatus(TradeMasterImportStatus.SUCCESS);
                history.setFailureReason(null);
                history.setFinishedAt(Instant.now());
                updateLock(history.getMasterType(), history.getId(), TradeMasterImportStatus.SUCCESS);
            });
        } catch (RuntimeException ex) {
            markAsyncFailed(historyId, ex);
        }
    }

    private List<TradeMasterImportRowDto> parseSources(
            TradeMasterType masterType,
            List<TradeMasterSourceDownloader.DownloadedMasterSource> sources
    ) {
        List<TradeMasterImportRowDto> rows = new ArrayList<>();
        for (TradeMasterSourceDownloader.DownloadedMasterSource source : sources) {
            rows.addAll(masterParser.parse(masterType, source.getContent()));
        }
        return rows;
    }

    private String sourceFileName(List<TradeMasterSourceDownloader.DownloadedMasterSource> sources) {
        if (sources.size() == 1) {
            return sources.getFirst().getSourceFileName();
        }
        return truncate(
                sources.size() + "_files:" + sources.stream()
                        .map(TradeMasterSourceDownloader.DownloadedMasterSource::getSourceFileName)
                        .reduce((left, right) -> left + "," + right)
                        .orElse("UNKNOWN"),
                200
        );
    }

    private void markAsyncFailed(Long historyId, RuntimeException ex) {
        transactionTemplate.executeWithoutResult(status -> historyRepository.findById(historyId).ifPresent(history -> {
            history.setSuccess(false);
            history.setImportStatus(TradeMasterImportStatus.FAILED);
            history.setFailureReason(limit(ex.getMessage()));
            history.setFinishedAt(Instant.now());
            updateLock(history.getMasterType(), history.getId(), TradeMasterImportStatus.FAILED);
        }));
    }

    private void updateLock(TradeMasterType masterType, Long historyId, TradeMasterImportStatus importStatus) {
        TradeMasterImportLock lock = lockForUpdate(masterType);
        lock.setImportStatus(importStatus);
        lock.setHistoryId(historyId);
        lock.setUpdatedAt(Instant.now());
    }

    private void saveFailedHistory(
            TradeMasterType masterType,
            String sourceFileName,
            String sourceVersion,
            Instant startedAt,
            RuntimeException ex
    ) {
        TradeMasterImportHistory history = new TradeMasterImportHistory();
        history.setMasterType(masterType);
        history.setSourceFileName(sourceFileName == null || sourceFileName.isBlank() ? "UNKNOWN" : truncate(sourceFileName, 200));
        history.setSourceVersion(sourceVersion);
        history.setImportStatus(TradeMasterImportStatus.FAILED);
        history.setImportedCount(0);
        history.setStartedAt(startedAt);
        history.setFinishedAt(Instant.now());
        history.setSuccess(false);
        history.setFailureReason(limit(ex.getMessage()));
        historyRepository.save(history);
    }

    private void replaceMasterRows(TradeMasterImportRequest request, Instant syncedAt) {
        jdbcTemplate.update("delete from " + itemMasterTable + " where master_type = ?", request.getMasterType().name());
        bulkInsert(request, syncedAt);
    }

    private void bulkInsert(TradeMasterImportRequest request, Instant syncedAt) {
        String sql = """
                insert into %s (
                    master_type, item_code, item_name, market_code, country_code, sector_name,
                    per, pbr, eps, bps, sales_amount, operating_profit, market_cap,
                    high52_week_price, low52_week_price, source_file_name, source_downloaded_at,
                    source_version, raw_json, synced_at
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.formatted(itemMasterTable);
        jdbcTemplate.batchUpdate(sql, request.getRows(), 500, (ps, row) -> {
            ps.setString(1, request.getMasterType().name());
            ps.setString(2, row.getItemCode());
            ps.setString(3, row.getItemName());
            ps.setString(4, row.getMarketCode());
            ps.setString(5, row.getCountryCode());
            ps.setString(6, row.getSectorName());
            ps.setBigDecimal(7, row.getPer());
            ps.setBigDecimal(8, row.getPbr());
            ps.setBigDecimal(9, row.getEps());
            ps.setBigDecimal(10, row.getBps());
            ps.setBigDecimal(11, row.getSalesAmount());
            ps.setBigDecimal(12, row.getOperatingProfit());
            ps.setBigDecimal(13, row.getMarketCap());
            ps.setBigDecimal(14, row.getHigh52WeekPrice());
            ps.setBigDecimal(15, row.getLow52WeekPrice());
            ps.setString(16, request.getSourceFileName());
            ps.setTimestamp(17, Timestamp.from(syncedAt));
            ps.setString(18, request.getSourceVersion());
            ps.setString(19, rawJson(row));
            ps.setTimestamp(20, Timestamp.from(syncedAt));
        });
    }

    private String rawJson(TradeMasterImportRowDto row) {
        if (row.getRaw() == null || row.getRaw().isEmpty()) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(row.getRaw());
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("invalid raw payload", ex);
        }
    }

    private String tableName(String schema, String table) {
        if (schema == null || schema.isBlank()) {
            return table;
        }
        if (!schema.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException("invalid database schema: " + schema);
        }
        return schema + "." + table;
    }

    private String sourceLabel(TradeMasterDownloadImportRequest request) {
        String source = request.getSourceUrl();
        if (source == null || source.isBlank()) {
            return "DEFAULT_" + request.getMasterType().name();
        }
        return truncate(source, 200);
    }

    private String truncate(String value, int limit) {
        if (value == null || value.length() <= limit) {
            return value;
        }
        return value.substring(0, limit);
    }

    private String limit(String value) {
        return truncate(value == null ? "unknown failure" : value, 1000);
    }

    private TradeMasterImportStatus statusOf(TradeMasterImportHistory history) {
        if (history.getImportStatus() != null) {
            return history.getImportStatus();
        }
        return history.isSuccess() ? TradeMasterImportStatus.SUCCESS : TradeMasterImportStatus.FAILED;
    }

    private TradeMasterImportJobDto toJob(TradeMasterImportHistory history) {
        TradeMasterImportJobDto dto = new TradeMasterImportJobDto();
        dto.setHistoryId(history.getId());
        dto.setMasterType(history.getMasterType());
        dto.setImportStatus(statusOf(history));
        dto.setSubmittedAt(history.getStartedAt());
        dto.setMessage("master import accepted");
        return dto;
    }

    private TradeMasterImportResultDto toResult(TradeMasterImportHistory history) {
        TradeMasterImportResultDto dto = new TradeMasterImportResultDto();
        dto.setHistoryId(history.getId());
        dto.setMasterType(history.getMasterType());
        dto.setSourceFileName(history.getSourceFileName());
        dto.setSourceVersion(history.getSourceVersion());
        dto.setImportStatus(statusOf(history));
        dto.setImportedCount(history.getImportedCount());
        dto.setStartedAt(history.getStartedAt());
        dto.setFinishedAt(history.getFinishedAt());
        dto.setSuccess(history.isSuccess());
        dto.setFailureReason(history.getFailureReason());
        return dto;
    }

}
