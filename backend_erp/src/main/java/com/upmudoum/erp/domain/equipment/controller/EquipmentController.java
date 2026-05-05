package com.upmudoum.erp.domain.equipment.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.equipment.dto.EquipmentProcessRequest;
import com.upmudoum.erp.domain.equipment.dto.EquipmentProcessResponse;
import com.upmudoum.erp.domain.equipment.dto.EquipmentRequest;
import com.upmudoum.erp.domain.equipment.dto.EquipmentResponse;
import com.upmudoum.erp.domain.equipment.service.EquipmentService;
import com.upmudoum.erp.domain.equipment.vo.EquipmentStatus;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/erp/equipments")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping
    public ApiResponse<EquipmentResponse> create(@Valid @RequestBody EquipmentRequest request) {
        return ApiResponse.ok(equipmentService.create(request));
    }

    @GetMapping
    public ApiResponse<List<EquipmentResponse>> findAll() {
        return ApiResponse.ok(equipmentService.findAll());
    }

    @GetMapping("/search")
    public ApiResponse<List<EquipmentResponse>> search(@RequestParam(required = false) String equipmentType,
                                                       @RequestParam(required = false) EquipmentStatus status) {
        return ApiResponse.ok(equipmentService.search(equipmentType, status));
    }

    @PutMapping("/{id}")
    public ApiResponse<EquipmentResponse> update(@PathVariable Long id, @Valid @RequestBody EquipmentRequest request) {
        return ApiResponse.ok(equipmentService.update(id, request));
    }

    @PostMapping("/{equipmentId}/processes")
    public ApiResponse<EquipmentProcessResponse> addProcess(@PathVariable Long equipmentId,
                                                            @Valid @RequestBody EquipmentProcessRequest request) {
        return ApiResponse.ok(equipmentService.addProcess(equipmentId, request));
    }

    @DeleteMapping("/{equipmentId}/processes/{equipmentProcessId}")
    public ApiResponse<Void> disableProcess(@PathVariable Long equipmentId, @PathVariable Long equipmentProcessId) {
        equipmentService.disableProcess(equipmentProcessId);
        return ApiResponse.ok(null);
    }

    @GetMapping("/{equipmentId}/processes")
    public ApiResponse<List<EquipmentProcessResponse>> findProcesses(@PathVariable Long equipmentId) {
        return ApiResponse.ok(equipmentService.findProcesses(equipmentId));
    }
}
