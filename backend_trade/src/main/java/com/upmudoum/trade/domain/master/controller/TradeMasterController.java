package com.upmudoum.trade.domain.master.controller;

import com.upmudoum.trade.domain.master.dto.TradeMasterDownloadImportRequest;
import com.upmudoum.trade.domain.master.dto.TradeMasterImportHistoryDto;
import com.upmudoum.trade.domain.master.dto.TradeMasterImportJobDto;
import com.upmudoum.trade.domain.master.dto.TradeMasterImportRequest;
import com.upmudoum.trade.domain.master.dto.TradeMasterImportResultDto;
import com.upmudoum.trade.domain.master.dto.TradeMasterStatusDto;
import com.upmudoum.trade.domain.master.dto.TradeMasterTypeDto;
import com.upmudoum.trade.domain.master.service.TradeMasterImportService;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/masters")
public class TradeMasterController {

    private final TradeMasterImportService importService;

    public TradeMasterController(TradeMasterImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/import")
    public TradeMasterImportResultDto importMaster(@Valid @RequestBody TradeMasterImportRequest request) {
        return importService.importMaster(request);
    }

    @PostMapping("/download-import")
    public TradeMasterImportResultDto downloadImport(@Valid @RequestBody TradeMasterDownloadImportRequest request) {
        return importService.downloadAndImport(request);
    }

    @PostMapping("/download-import/async")
    public TradeMasterImportJobDto downloadImportAsync(@Valid @RequestBody TradeMasterDownloadImportRequest request) {
        return importService.downloadAndImportAsync(request);
    }

    @PostMapping("/download-import/defaults")
    public List<TradeMasterImportResultDto> downloadImportDefaults() {
        return importService.downloadAndImportDefaults();
    }

    @PostMapping("/download-import/defaults/async")
    public List<TradeMasterImportJobDto> downloadImportDefaultsAsync() {
        return importService.downloadAndImportDefaultsAsync();
    }

    @GetMapping("/status")
    public List<TradeMasterStatusDto> statuses() {
        return importService.statuses();
    }

    @GetMapping("/types")
    public List<TradeMasterTypeDto> types() {
        return importService.types();
    }

    @GetMapping("/types/{masterType}")
    public TradeMasterTypeDto type(@PathVariable TradeMasterType masterType) {
        return importService.type(masterType);
    }

    @GetMapping("/import-histories")
    public List<TradeMasterImportHistoryDto> histories(@RequestParam(required = false) TradeMasterType masterType) {
        return importService.histories(masterType);
    }
}
