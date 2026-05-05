package com.upmudoum.user.domain.department.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.domain.department.Department;
import com.upmudoum.user.domain.department.DepartmentRepository;
import com.upmudoum.user.domain.department.dto.DepartmentDtos.DepartmentRequest;
import com.upmudoum.user.domain.department.dto.DepartmentDtos.DepartmentTreeResponse;
import com.upmudoum.user.domain.userposition.service.UserPositionManagementService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DepartmentManagementServiceTest {

    private DepartmentRepository departmentRepository;
    private UserPositionManagementService userPositionManagementService;
    private DepartmentManagementService departmentManagementService;

    @BeforeEach
    void setUp() {
        departmentRepository = mock(DepartmentRepository.class);
        userPositionManagementService = mock(UserPositionManagementService.class);
        departmentManagementService = new DepartmentManagementService(departmentRepository, userPositionManagementService);
    }

    @Test
    void departmentTreeBuildsNestedOrganizationStructure() {
        when(departmentRepository.findByComCdAndEnabledOrderBySortSeqAscDepartmentIdAsc("COM001", true))
                .thenReturn(List.of(department("ROOT", null), department("DEV", "ROOT")));

        List<DepartmentTreeResponse> result = departmentManagementService.departmentTree("COM001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartmentId()).isEqualTo("ROOT");
        assertThat(result.get(0).getChildren()).extracting(DepartmentTreeResponse::getDepartmentId).containsExactly("DEV");
    }

    @Test
    void saveDepartmentsSyncsDepartmentHeadToUserPosition() {
        DepartmentRequest added = new DepartmentRequest("COM001", "FIN", "Finance", null, "kim", "LEAD", 1, true);

        departmentManagementService.saveDepartments(new BulkRequestDto<>(List.of(added), List.of(), List.of()));

        verify(userPositionManagementService).syncDepartmentHead("COM001", "FIN", null, null, "kim", "LEAD", true);
    }

    @Test
    void saveDepartmentsRemovesDepartmentPositionsWhenDepartmentIsDeleted() {
        DepartmentRequest deleted = new DepartmentRequest("COM001", "FIN", "Finance", null, null, null, 1, true);

        departmentManagementService.saveDepartments(new BulkRequestDto<>(List.of(), List.of(), List.of(deleted)));

        verify(userPositionManagementService).deleteDepartmentPositions("COM001", "FIN");
        verify(departmentRepository).deleteByComCdAndDepartmentId("COM001", "FIN");
    }

    @Test
    void saveDepartmentsSyncsChangedDepartmentHead() {
        Department department = new Department("COM001", "FIN", "Finance");
        department.update(null, "Finance", "lee", "HEAD", 1, true);
        when(departmentRepository.findByComCdAndDepartmentId("COM001", "FIN")).thenReturn(Optional.of(department));
        DepartmentRequest updated = new DepartmentRequest("COM001", "FIN", "Finance", null, "kim", "LEAD", 1, true);

        departmentManagementService.saveDepartments(new BulkRequestDto<>(List.of(), List.of(updated), List.of()));

        verify(userPositionManagementService).syncDepartmentHead("COM001", "FIN", "lee", "HEAD", "kim", "LEAD", true);
    }

    private Department department(String departmentId, String parentDepartmentId) {
        Department department = new Department("COM001", departmentId, departmentId + " Name");
        department.update(parentDepartmentId, departmentId + " Name", null, null, 1, true);
        return department;
    }
}
