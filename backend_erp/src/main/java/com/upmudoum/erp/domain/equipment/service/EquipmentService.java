package com.upmudoum.erp.domain.equipment.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.equipment.dto.EquipmentProcessRequest;
import com.upmudoum.erp.domain.equipment.dto.EquipmentProcessResponse;
import com.upmudoum.erp.domain.equipment.dto.EquipmentRequest;
import com.upmudoum.erp.domain.equipment.dto.EquipmentResponse;
import com.upmudoum.erp.domain.equipment.entity.Equipment;
import com.upmudoum.erp.domain.equipment.entity.EquipmentProcess;
import com.upmudoum.erp.domain.equipment.repository.EquipmentProcessRepository;
import com.upmudoum.erp.domain.equipment.repository.EquipmentRepository;
import com.upmudoum.erp.domain.equipment.vo.EquipmentStatus;
import com.upmudoum.erp.domain.process.entity.ErpProcess;
import com.upmudoum.erp.domain.process.repository.ErpProcessRepository;
import com.upmudoum.erp.domain.warehouse.entity.Warehouse;
import com.upmudoum.erp.domain.warehouse.repository.WarehouseRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentProcessRepository equipmentProcessRepository;
    private final ErpProcessRepository processRepository;
    private final WarehouseRepository warehouseRepository;

    public EquipmentService(EquipmentRepository equipmentRepository, EquipmentProcessRepository equipmentProcessRepository,
                            ErpProcessRepository processRepository, WarehouseRepository warehouseRepository) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentProcessRepository = equipmentProcessRepository;
        this.processRepository = processRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional
    public EquipmentResponse create(EquipmentRequest request) {
        if (equipmentRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Equipment code already exists");
        }
        Warehouse warehouse = resolveWarehouse(request.getWarehouseId());
        return EquipmentResponse.from(equipmentRepository.save(new Equipment(
                request.getCode(), request.getName(), request.getEquipmentType(), warehouse,
                request.getLocation(), request.getStatus())));
    }

    public List<EquipmentResponse> findAll() {
        return equipmentRepository.findByEnabledTrueOrderByCodeAsc().stream()
                .map(EquipmentResponse::from)
                .toList();
    }

    public List<EquipmentResponse> search(String equipmentType, EquipmentStatus status) {
        if (equipmentType == null || status == null) {
            return findAll();
        }
        return equipmentRepository.findByEquipmentTypeAndStatusAndEnabledTrueOrderByCodeAsc(equipmentType, status).stream()
                .map(EquipmentResponse::from)
                .toList();
    }

    @Transactional
    public EquipmentResponse update(Long id, EquipmentRequest request) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow(() -> new BusinessException("Equipment not found"));
        equipment.update(request.getName(), request.getEquipmentType(), resolveWarehouse(request.getWarehouseId()),
                request.getLocation(), request.getStatus(), request.isEnabled());
        return EquipmentResponse.from(equipment);
    }

    @Transactional
    public EquipmentProcessResponse addProcess(Long equipmentId, EquipmentProcessRequest request) {
        if (equipmentProcessRepository.existsByEquipmentIdAndProcessIdAndEnabledTrue(equipmentId, request.getProcessId())) {
            throw new BusinessException("Equipment process mapping already exists");
        }
        Equipment equipment = equipmentRepository.findById(equipmentId).orElseThrow(() -> new BusinessException("Equipment not found"));
        ErpProcess process = processRepository.findById(request.getProcessId())
                .orElseThrow(() -> new BusinessException("Process not found"));
        return EquipmentProcessResponse.from(equipmentProcessRepository.save(new EquipmentProcess(equipment, process)));
    }

    @Transactional
    public void disableProcess(Long equipmentProcessId) {
        EquipmentProcess mapping = equipmentProcessRepository.findById(equipmentProcessId)
                .orElseThrow(() -> new BusinessException("Equipment process mapping not found"));
        mapping.disable();
    }

    public List<EquipmentProcessResponse> findProcesses(Long equipmentId) {
        return equipmentProcessRepository.findByEquipmentIdAndEnabledTrueOrderByIdAsc(equipmentId).stream()
                .map(EquipmentProcessResponse::from)
                .toList();
    }

    private Warehouse resolveWarehouse(Long warehouseId) {
        if (warehouseId == null) {
            return null;
        }
        return warehouseRepository.findById(warehouseId).orElseThrow(() -> new BusinessException("Warehouse not found"));
    }
}
