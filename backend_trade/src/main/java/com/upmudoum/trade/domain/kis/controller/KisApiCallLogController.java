package com.upmudoum.trade.domain.kis.controller;

import com.upmudoum.trade.domain.kis.dto.KisApiCallLogDto;
import com.upmudoum.trade.domain.kis.service.KisApiCallLogService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/kis")
public class KisApiCallLogController {

    private final KisApiCallLogService logService;

    public KisApiCallLogController(KisApiCallLogService logService) {
        this.logService = logService;
    }

    @GetMapping("/call-logs")
    public List<KisApiCallLogDto> callLogs() {
        return logService.findAll();
    }
}
