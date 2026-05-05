package com.upmudoum.trade.domain.master.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.trade.config.MasterImportAsyncConfig;
import com.upmudoum.trade.config.QuerydslConfig;
import com.upmudoum.trade.domain.item.repository.ItemMasterRepository;
import com.upmudoum.trade.domain.master.dto.TradeMasterImportRequest;
import com.upmudoum.trade.domain.master.dto.TradeMasterImportRowDto;
import com.upmudoum.trade.domain.master.dto.TradeMasterDownloadImportRequest;
import com.upmudoum.trade.domain.master.entity.TradeMasterImportHistory;
import com.upmudoum.trade.domain.master.entity.TradeMasterImportLock;
import com.upmudoum.trade.domain.master.infra.TradeMasterParser;
import com.upmudoum.trade.domain.master.infra.TradeMasterSourceDownloader;
import com.upmudoum.trade.domain.master.querydsl.TradeMasterImportHistoryQueryRepository;
import com.upmudoum.trade.domain.master.repository.TradeMasterImportHistoryRepository;
import com.upmudoum.trade.domain.master.repository.TradeMasterImportLockRepository;
import com.upmudoum.trade.domain.master.vo.TradeMasterImportStatus;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.server.ResponseStatusException;

@DataJpaTest
@Import({
        MasterImportAsyncConfig.class,
        QuerydslConfig.class,
        TradeMasterImportService.class,
        TradeMasterSourceRegistry.class,
        TradeMasterImportHistoryQueryRepository.class,
        TradeMasterImportServiceTests.TestConfig.class
})
class TradeMasterImportServiceTests {

    @Autowired
    private TradeMasterImportService importService;

    @Autowired
    private ItemMasterRepository itemMasterRepository;

    @Autowired
    private TradeMasterImportHistoryRepository historyRepository;

    @Autowired
    private TradeMasterImportLockRepository lockRepository;

    @Test
    void importMasterBulkInsertUsesJpaColumnNames() {
        TradeMasterImportRequest request = new TradeMasterImportRequest();
        request.setMasterType(TradeMasterType.KOSPI);
        request.setSourceFileName("kospi_code.mst.zip");
        request.setSourceVersion("2026-05-03");
        request.setRows(List.of(row()));

        importService.importMaster(request);

        assertThat(itemMasterRepository.findByItemCode("005930"))
                .hasValueSatisfying(item -> {
                    assertThat(item.getHigh52WeekPrice()).isEqualByComparingTo("85000");
                    assertThat(item.getLow52WeekPrice()).isEqualByComparingTo("62000");
                });
        assertThat(historyRepository.findTop50ByMasterTypeOrderByStartedAtDesc(TradeMasterType.KOSPI))
                .first()
                .satisfies(history -> {
                    assertThat(history.isSuccess()).isTrue();
                    assertThat(history.getImportStatus().name()).isEqualTo("SUCCESS");
                    assertThat(history.getImportedCount()).isEqualTo(1);
                });
    }

    @Test
    void downloadImportRejectsSameMasterTypeWithinOneMinute() {
        saveLock(TradeMasterType.KOSPI, TradeMasterImportStatus.SUCCESS, Instant.now().minusSeconds(10));

        assertThatThrownBy(() -> importService.downloadAndImportAsync(downloadRequest(TradeMasterType.KOSPI)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("429 TOO_MANY_REQUESTS");
    }

    @Test
    void downloadImportRejectsAlreadyRunningMasterType() {
        saveLock(TradeMasterType.KOSPI, TradeMasterImportStatus.RUNNING, Instant.now().minusSeconds(120));

        assertThatThrownBy(() -> importService.downloadAndImportAsync(downloadRequest(TradeMasterType.KOSPI)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT");
    }

    private TradeMasterImportRowDto row() {
        TradeMasterImportRowDto row = new TradeMasterImportRowDto();
        row.setItemCode("005930");
        row.setItemName("Samsung Electronics");
        row.setMarketCode("KOSPI");
        row.setCountryCode("KR");
        row.setHigh52WeekPrice(new BigDecimal("85000"));
        row.setLow52WeekPrice(new BigDecimal("62000"));
        return row;
    }

    private TradeMasterDownloadImportRequest downloadRequest(TradeMasterType masterType) {
        TradeMasterDownloadImportRequest request = new TradeMasterDownloadImportRequest();
        request.setMasterType(masterType);
        request.setSourceVersion("2026-05-03");
        return request;
    }

    private void saveLock(TradeMasterType masterType, TradeMasterImportStatus status, Instant lastRequestedAt) {
        TradeMasterImportLock lock = new TradeMasterImportLock();
        lock.setMasterType(masterType);
        lock.setImportStatus(status);
        lock.setLastRequestedAt(lastRequestedAt);
        lock.setUpdatedAt(lastRequestedAt);
        lockRepository.save(lock);
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        TradeMasterSourceDownloader tradeMasterSourceDownloader(TradeMasterSourceRegistry sourceRegistry) {
            return new TradeMasterSourceDownloader(sourceRegistry);
        }

        @Bean
        TradeMasterParser tradeMasterParser(TradeMasterSourceRegistry sourceRegistry) {
            return new TradeMasterParser(sourceRegistry);
        }
    }
}
