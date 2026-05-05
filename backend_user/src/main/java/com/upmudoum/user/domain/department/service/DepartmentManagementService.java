package com.upmudoum.user.domain.department.service;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.exception.BusinessException;
import com.upmudoum.user.common.exception.ErrorCode;
import com.upmudoum.user.domain.department.Department;
import com.upmudoum.user.domain.department.DepartmentRepository;
import com.upmudoum.user.domain.department.dto.DepartmentDtos.DepartmentRequest;
import com.upmudoum.user.domain.department.dto.DepartmentDtos.DepartmentResponse;
import com.upmudoum.user.domain.department.dto.DepartmentDtos.DepartmentTreeResponse;
import com.upmudoum.user.domain.userposition.service.UserPositionManagementService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartmentManagementService {

    private final DepartmentRepository departmentRepository;
    private final UserPositionManagementService userPositionManagementService;

    public DepartmentManagementService(DepartmentRepository departmentRepository, UserPositionManagementService userPositionManagementService) {
        this.departmentRepository = departmentRepository;
        this.userPositionManagementService = userPositionManagementService;
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> departments(String comCd) {
        return departmentRepository.findByComCdOrderBySortSeqAscDepartmentIdAsc(comCd).stream().map(this::toDepartment).toList();
    }

    @Transactional(readOnly = true)
    public List<DepartmentTreeResponse> departmentTree(String comCd) {
        List<Department> departments = departmentRepository.findByComCdAndEnabledOrderBySortSeqAscDepartmentIdAsc(comCd, true);
        Map<String, List<Department>> childrenByParent = new LinkedHashMap<>();
        for (Department department : departments) {
            childrenByParent.computeIfAbsent(normalizeParentKey(department.getParentDepartmentId()), ignored -> new ArrayList<>()).add(department);
        }
        return childrenByParent.getOrDefault("", List.of()).stream()
                .map(department -> toDepartmentTree(department, childrenByParent, new HashSet<>()))
                .toList();
    }

    @Transactional
    public BulkResultDto saveDepartments(BulkRequestDto<DepartmentRequest> request) {
        request.getAdded().forEach(item -> {
            Department department = new Department(item.getComCd(), item.getDepartmentId(), item.getDepartmentName());
            department.update(item.getParentDepartmentId(), item.getDepartmentName(), item.getDepartmentHeadUserId(), item.getDepartmentHeadPositionId(), item.getSortSeq(), item.isEnabled());
            departmentRepository.save(department);
            userPositionManagementService.syncDepartmentHead(
                    item.getComCd(),
                    item.getDepartmentId(),
                    null,
                    null,
                    item.getDepartmentHeadUserId(),
                    item.getDepartmentHeadPositionId(),
                    item.isEnabled()
            );
        });
        request.getUpdated().forEach(item -> {
            Department department = departmentRepository.findByComCdAndDepartmentId(item.getComCd(), item.getDepartmentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Department was not found."));
            String previousHeadUserId = department.getDepartmentHeadUserId();
            String previousHeadPositionId = department.getDepartmentHeadPositionId();
            department.update(item.getParentDepartmentId(), item.getDepartmentName(), item.getDepartmentHeadUserId(), item.getDepartmentHeadPositionId(), item.getSortSeq(), item.isEnabled());
            userPositionManagementService.syncDepartmentHead(
                    item.getComCd(),
                    item.getDepartmentId(),
                    previousHeadUserId,
                    previousHeadPositionId,
                    item.getDepartmentHeadUserId(),
                    item.getDepartmentHeadPositionId(),
                    item.isEnabled()
            );
        });
        request.getDeleted().forEach(item -> {
            userPositionManagementService.deleteDepartmentPositions(item.getComCd(), item.getDepartmentId());
            departmentRepository.deleteByComCdAndDepartmentId(item.getComCd(), item.getDepartmentId());
        });
        return new BulkResultDto(request.getAdded().size(), request.getUpdated().size(), request.getDeleted().size());
    }

    private DepartmentResponse toDepartment(Department department) {
        return new DepartmentResponse(department.getComCd(), department.getDepartmentId(), department.getDepartmentName(), department.getParentDepartmentId(), department.getDepartmentHeadUserId(), department.getDepartmentHeadPositionId(), department.getSortSeq(), department.isEnabled());
    }

    private DepartmentTreeResponse toDepartmentTree(Department department, Map<String, List<Department>> childrenByParent, Set<String> visited) {
        if (!visited.add(department.getDepartmentId())) {
            return new DepartmentTreeResponse(department.getComCd(), department.getDepartmentId(), department.getDepartmentName(), department.getParentDepartmentId(), department.getDepartmentHeadUserId(), department.getDepartmentHeadPositionId(), department.getSortSeq(), department.isEnabled(), List.of());
        }
        List<DepartmentTreeResponse> children = childrenByParent.getOrDefault(department.getDepartmentId(), List.of())
                .stream()
                .map(child -> toDepartmentTree(child, childrenByParent, new HashSet<>(visited)))
                .toList();
        return new DepartmentTreeResponse(department.getComCd(), department.getDepartmentId(), department.getDepartmentName(), department.getParentDepartmentId(), department.getDepartmentHeadUserId(), department.getDepartmentHeadPositionId(), department.getSortSeq(), department.isEnabled(), children);
    }

    private String normalizeParentKey(String parentDepartmentId) {
        return parentDepartmentId == null || parentDepartmentId.isBlank() ? "" : parentDepartmentId;
    }
}
