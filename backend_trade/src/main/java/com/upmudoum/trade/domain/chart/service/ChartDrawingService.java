package com.upmudoum.trade.domain.chart.service;

import com.upmudoum.trade.domain.chart.dto.ChartDrawingDto;
import com.upmudoum.trade.domain.chart.dto.SaveChartDrawingRequest;
import com.upmudoum.trade.domain.chart.entity.ChartDrawing;
import com.upmudoum.trade.domain.chart.repository.ChartDrawingRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChartDrawingService {

    private final ChartDrawingRepository repository;

    public ChartDrawingService(ChartDrawingRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ChartDrawingDto> find(String userId, String itemCode) {
        return repository.findByUserIdAndItemCodeOrderByUpdatedAtDesc(userId, itemCode).stream().map(this::toDto).toList();
    }

    @Transactional
    public ChartDrawingDto create(String userId, String itemCode, SaveChartDrawingRequest request) {
        ChartDrawing drawing = new ChartDrawing();
        drawing.setUserId(userId);
        drawing.setItemCode(itemCode);
        apply(drawing, request);
        drawing.setCreatedAt(Instant.now());
        drawing.setUpdatedAt(drawing.getCreatedAt());
        return toDto(repository.save(drawing));
    }

    @Transactional
    public ChartDrawingDto update(String userId, String itemCode, Long drawingId, SaveChartDrawingRequest request) {
        ChartDrawing drawing = repository.findByIdAndUserIdAndItemCode(drawingId, userId, itemCode)
                .orElseThrow(() -> new IllegalArgumentException("chart drawing not found"));
        apply(drawing, request);
        drawing.setUpdatedAt(Instant.now());
        return toDto(repository.save(drawing));
    }

    @Transactional
    public void delete(String userId, String itemCode, Long drawingId) {
        ChartDrawing drawing = repository.findByIdAndUserIdAndItemCode(drawingId, userId, itemCode)
                .orElseThrow(() -> new IllegalArgumentException("chart drawing not found"));
        repository.delete(drawing);
    }

    private void apply(ChartDrawing drawing, SaveChartDrawingRequest request) {
        drawing.setDrawingType(request.getDrawingType());
        drawing.setStartDate(request.getStartDate());
        drawing.setStartPrice(request.getStartPrice());
        drawing.setEndDate(request.getEndDate());
        drawing.setEndPrice(request.getEndPrice());
        drawing.setMemo(request.getMemo());
    }

    private ChartDrawingDto toDto(ChartDrawing drawing) {
        ChartDrawingDto dto = new ChartDrawingDto();
        dto.setId(drawing.getId());
        dto.setUserId(drawing.getUserId());
        dto.setItemCode(drawing.getItemCode());
        dto.setDrawingType(drawing.getDrawingType());
        dto.setStartDate(drawing.getStartDate());
        dto.setStartPrice(drawing.getStartPrice());
        dto.setEndDate(drawing.getEndDate());
        dto.setEndPrice(drawing.getEndPrice());
        dto.setMemo(drawing.getMemo());
        dto.setCreatedAt(drawing.getCreatedAt());
        dto.setUpdatedAt(drawing.getUpdatedAt());
        return dto;
    }
}
