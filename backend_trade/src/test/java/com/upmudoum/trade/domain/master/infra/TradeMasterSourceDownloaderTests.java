package com.upmudoum.trade.domain.master.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.upmudoum.trade.domain.master.service.TradeMasterSourceRegistry;
import org.junit.jupiter.api.Test;

class TradeMasterSourceDownloaderTests {

    private final TradeMasterSourceDownloader downloader = new TradeMasterSourceDownloader(new TradeMasterSourceRegistry());

    @Test
    void httpsZipSourceIsRemoteNotLocal() {
        String source = "https://new.real.download.dws.co.kr/common/master/kospi_code.mst.zip";

        assertThat(downloader.isHttpUrl(source)).isTrue();
        assertThat(downloader.shouldReadLocal(source)).isFalse();
    }

    @Test
    void localZipSourceIsLocal() {
        String source = "C:\\workSpace\\masters\\kospi_code.mst.zip";

        assertThat(downloader.isHttpUrl(source)).isFalse();
        assertThat(downloader.shouldReadLocal(source)).isTrue();
    }
}
