package com.upmudoum.user.domain.dropdown.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.upmudoum.user.domain.code.CommonCode;
import com.upmudoum.user.domain.code.CommonCodeRepository;
import com.upmudoum.user.domain.department.Department;
import com.upmudoum.user.domain.department.DepartmentRepository;
import com.upmudoum.user.domain.dropdown.dto.DropdownOptionResponse;
import com.upmudoum.user.domain.dropdown.dto.DropdownUserOptionRow;
import com.upmudoum.user.domain.dropdown.querydsl.DropdownQueryRepository;
import com.upmudoum.user.domain.jobgrade.JobGrade;
import com.upmudoum.user.domain.jobgrade.JobGradeRepository;
import com.upmudoum.user.domain.position.Position;
import com.upmudoum.user.domain.position.PositionRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DropdownServiceTest {

    private DepartmentRepository departmentRepository;
    private JobGradeRepository jobGradeRepository;
    private PositionRepository positionRepository;
    private CommonCodeRepository commonCodeRepository;
    private DropdownQueryRepository dropdownQueryRepository;
    private DropdownService dropdownService;

    @BeforeEach
    void setUp() {
        departmentRepository = mock(DepartmentRepository.class);
        jobGradeRepository = mock(JobGradeRepository.class);
        positionRepository = mock(PositionRepository.class);
        commonCodeRepository = mock(CommonCodeRepository.class);
        dropdownQueryRepository = mock(DropdownQueryRepository.class);
        dropdownService = new DropdownService(departmentRepository, jobGradeRepository, positionRepository, commonCodeRepository, dropdownQueryRepository);
    }

    @Test
    void getDepartmentsReturnsNameAsLabelAndIdAsValue() {
        when(departmentRepository.findByComCdAndEnabledOrderBySortSeqAscDepartmentIdAsc("COM001", true))
                .thenReturn(List.of(new Department("COM001", "FIN", "Finance")));

        List<DropdownOptionResponse> result = dropdownService.getDepartments("COM001");

        assertThat(result).extracting(DropdownOptionResponse::getLabel).containsExactly("Finance");
        assertThat(result).extracting(DropdownOptionResponse::getValue).containsExactly("FIN");
    }

    @Test
    void getUserListIncludesPhoneAndJobGradeNameInLabel() {
        DropdownUserOptionRow row = new DropdownUserOptionRow();
        row.setUserId("kim");
        row.setUserName("Kim");
        row.setPhone("010-1111-2222");
        row.setJobGradeId("MGR");
        row.setJobGradeName("Manager");
        when(dropdownQueryRepository.findUserDropdownRows("COM001")).thenReturn(List.of(row));

        List<DropdownOptionResponse> result = dropdownService.getUserList("COM001");

        assertThat(result).extracting(DropdownOptionResponse::getLabel).containsExactly("Kim / 010-1111-2222 / Manager");
        assertThat(result).extracting(DropdownOptionResponse::getValue).containsExactly("kim");
    }

    @Test
    void getCodeListReturnsCodeNameAsLabelAndCodeIdAsValue() {
        when(commonCodeRepository.findByComCdAndServiceIdAndCodeGroupIdAndEnabledOrderBySortOrderAsc("COM001", "ERP", "STATUS", true))
                .thenReturn(List.of(new CommonCode("COM001", "ERP", "STATUS", "ACTIVE", "Active", 1)));

        List<DropdownOptionResponse> result = dropdownService.getCodeList("COM001", "ERP", "STATUS");

        assertThat(result).extracting(DropdownOptionResponse::getLabel).containsExactly("Active");
        assertThat(result).extracting(DropdownOptionResponse::getValue).containsExactly("ACTIVE");
    }
}
