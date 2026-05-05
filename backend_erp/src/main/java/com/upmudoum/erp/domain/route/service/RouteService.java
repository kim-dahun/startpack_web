package com.upmudoum.erp.domain.route.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.equipment.entity.Equipment;
import com.upmudoum.erp.domain.equipment.repository.EquipmentRepository;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.item.repository.ItemRepository;
import com.upmudoum.erp.domain.process.entity.ErpProcess;
import com.upmudoum.erp.domain.process.repository.ErpProcessRepository;
import com.upmudoum.erp.domain.route.dto.RouteRequest;
import com.upmudoum.erp.domain.route.dto.RouteResponse;
import com.upmudoum.erp.domain.route.dto.RouteStepRequest;
import com.upmudoum.erp.domain.route.dto.RouteStepResponse;
import com.upmudoum.erp.domain.route.entity.Route;
import com.upmudoum.erp.domain.route.entity.RouteStep;
import com.upmudoum.erp.domain.route.repository.RouteRepository;
import com.upmudoum.erp.domain.route.repository.RouteStepRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RouteService {

    private final RouteRepository routeRepository;
    private final RouteStepRepository routeStepRepository;
    private final ItemRepository itemRepository;
    private final ErpProcessRepository processRepository;
    private final EquipmentRepository equipmentRepository;

    public RouteService(RouteRepository routeRepository, RouteStepRepository routeStepRepository,
                        ItemRepository itemRepository, ErpProcessRepository processRepository,
                        EquipmentRepository equipmentRepository) {
        this.routeRepository = routeRepository;
        this.routeStepRepository = routeStepRepository;
        this.itemRepository = itemRepository;
        this.processRepository = processRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Transactional
    public RouteResponse create(RouteRequest request) {
        if (routeRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Route code already exists");
        }
        return RouteResponse.from(routeRepository.save(new Route(request.getCode(), request.getName(),
                resolveItem(request.getItemId()))));
    }

    public List<RouteResponse> findAll() {
        return routeRepository.findByEnabledTrueOrderByCodeAsc().stream()
                .map(RouteResponse::from)
                .toList();
    }

    public List<RouteResponse> search(Long itemId) {
        if (itemId == null) {
            return findAll();
        }
        return routeRepository.findByItemIdAndEnabledTrueOrderByCodeAsc(itemId).stream()
                .map(RouteResponse::from)
                .toList();
    }

    @Transactional
    public RouteResponse update(Long routeId, RouteRequest request) {
        Route route = routeRepository.findById(routeId).orElseThrow(() -> new BusinessException("Route not found"));
        route.update(request.getName(), resolveItem(request.getItemId()), request.isEnabled());
        return RouteResponse.from(route);
    }

    @Transactional
    public RouteStepResponse addStep(Long routeId, RouteStepRequest request) {
        if (routeStepRepository.existsByRouteIdAndSequenceNoAndEnabledTrue(routeId, request.getSequenceNo())) {
            throw new BusinessException("Route step sequence already exists");
        }
        Route route = routeRepository.findById(routeId).orElseThrow(() -> new BusinessException("Route not found"));
        ErpProcess process = processRepository.findById(request.getProcessId())
                .orElseThrow(() -> new BusinessException("Process not found"));
        Equipment equipment = resolveEquipment(request.getDefaultEquipmentId());
        return RouteStepResponse.from(routeStepRepository.save(new RouteStep(route, request.getSequenceNo(), process,
                equipment, request.getStandardLeadTimeMinutes(), request.getDescription())));
    }

    @Transactional
    public RouteStepResponse updateStep(Long routeId, Long routeStepId, RouteStepRequest request) {
        RouteStep step = routeStepRepository.findById(routeStepId)
                .orElseThrow(() -> new BusinessException("Route step not found"));
        if (!step.getRoute().getId().equals(routeId)) {
            throw new BusinessException("Route step does not belong to route");
        }
        ErpProcess process = processRepository.findById(request.getProcessId())
                .orElseThrow(() -> new BusinessException("Process not found"));
        step.update(request.getSequenceNo(), process, resolveEquipment(request.getDefaultEquipmentId()),
                request.getStandardLeadTimeMinutes(), request.getDescription(), request.isEnabled());
        return RouteStepResponse.from(step);
    }

    @Transactional
    public void disableStep(Long routeId, Long routeStepId) {
        RouteStep step = routeStepRepository.findById(routeStepId)
                .orElseThrow(() -> new BusinessException("Route step not found"));
        if (!step.getRoute().getId().equals(routeId)) {
            throw new BusinessException("Route step does not belong to route");
        }
        step.disable();
    }

    public List<RouteStepResponse> findSteps(Long routeId) {
        return routeStepRepository.findByRouteIdAndEnabledTrueOrderBySequenceNoAscIdAsc(routeId).stream()
                .map(RouteStepResponse::from)
                .toList();
    }

    private Item resolveItem(Long itemId) {
        if (itemId == null) {
            return null;
        }
        return itemRepository.findById(itemId).orElseThrow(() -> new BusinessException("Item not found"));
    }

    private Equipment resolveEquipment(Long equipmentId) {
        if (equipmentId == null) {
            return null;
        }
        return equipmentRepository.findById(equipmentId).orElseThrow(() -> new BusinessException("Equipment not found"));
    }
}
