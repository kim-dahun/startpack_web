package com.upmudoum.trade.domain.master.infra;

import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import com.upmudoum.trade.domain.master.service.TradeMasterSourceRegistry;
import com.upmudoum.trade.domain.master.vo.TradeMasterSourceDefinition;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.zip.ZipInputStream;
import org.springframework.stereotype.Component;

@Component
public class TradeMasterSourceDownloader {

    private final TradeMasterSourceRegistry sourceRegistry;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public TradeMasterSourceDownloader(TradeMasterSourceRegistry sourceRegistry) {
        this.sourceRegistry = sourceRegistry;
    }

    public DownloadedMasterSource download(TradeMasterType masterType, String overrideUrl) {
        return downloadAll(masterType, overrideUrl).getFirst();
    }

    public List<DownloadedMasterSource> downloadAll(TradeMasterType masterType, String overrideUrl) {
        TradeMasterSourceDefinition definition = sourceRegistry.get(masterType);
        if (!definition.isEnabled()) {
            throw new IllegalArgumentException("master source is disabled for " + masterType);
        }
        List<String> urls = overrideUrl == null || overrideUrl.isBlank()
                ? defaultUrls(definition)
                : List.of(overrideUrl);
        return urls.stream()
                .map(this::downloadSource)
                .toList();
    }

    private DownloadedMasterSource downloadSource(String url) {
        if (shouldReadLocal(url)) {
            return readLocal(url);
        }
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(60))
                .GET()
                .build();
        try {
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("master download failed: HTTP " + response.statusCode());
            }
            return new DownloadedMasterSource(fileName(url), unzipFirstEntry(response.body()));
        } catch (IOException ex) {
            throw new IllegalStateException("master download failed", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("master download interrupted", ex);
        }
    }

    boolean isHttpUrl(String source) {
        String normalized = source.toLowerCase();
        return normalized.startsWith("http://") || normalized.startsWith("https://");
    }

    boolean isLocalPath(String source) {
        return source.startsWith("file:") || source.contains("\\") || source.startsWith("/") || source.endsWith(".mst") || source.endsWith(".zip");
    }

    boolean shouldReadLocal(String source) {
        return !isHttpUrl(source) && isLocalPath(source);
    }

    private DownloadedMasterSource readLocal(String source) {
        try {
            Path path = source.startsWith("file:") ? Path.of(URI.create(source)) : Path.of(source);
            return new DownloadedMasterSource(path.getFileName().toString(), unzipFirstEntry(Files.readAllBytes(path)));
        } catch (IOException ex) {
            throw new IllegalStateException("master local file read failed", ex);
        }
    }

    private List<String> defaultUrls(TradeMasterSourceDefinition definition) {
        List<String> sourceUrls = sourceRegistry.sourceUrls(definition.getMasterType());
        if (sourceUrls.isEmpty() || sourceUrls.stream().anyMatch(sourceUrl -> sourceUrl.contains("{"))) {
            throw new IllegalArgumentException("sourceUrl is required for " + definition.getMasterType());
        }
        return sourceUrls;
    }

    private byte[] unzipFirstEntry(byte[] content) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(content))) {
            if (zipInputStream.getNextEntry() == null) {
                return content;
            }
            return zipInputStream.readAllBytes();
        } catch (IOException ex) {
            return content;
        }
    }

    private String fileName(String url) {
        int index = url.lastIndexOf('/');
        return index < 0 ? url : url.substring(index + 1);
    }

    public static class DownloadedMasterSource {

        private final String sourceFileName;
        private final byte[] content;

        public DownloadedMasterSource(String sourceFileName, byte[] content) {
            this.sourceFileName = sourceFileName;
            this.content = content;
        }

        public String getSourceFileName() {
            return sourceFileName;
        }

        public byte[] getContent() {
            return content;
        }
    }
}
