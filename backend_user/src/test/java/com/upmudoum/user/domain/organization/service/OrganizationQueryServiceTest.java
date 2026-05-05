package com.upmudoum.user.domain.organization.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.upmudoum.user.domain.department.Department;
import com.upmudoum.user.domain.department.DepartmentRepository;
import com.upmudoum.user.domain.jobgrade.JobGradeRepository;
import com.upmudoum.user.domain.organization.dto.OrganizationDtos.OrganizationUserResponse;
import com.upmudoum.user.domain.position.Position;
import com.upmudoum.user.domain.position.PositionRepository;
import com.upmudoum.user.domain.user.UserAccount;
import com.upmudoum.user.domain.user.UserAccountRepository;
import com.upmudoum.user.domain.user.UserStatus;
import com.upmudoum.user.domain.userposition.UserPosition;
import com.upmudoum.user.domain.userposition.UserPositionRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrganizationQueryServiceTest {

    private UserAccountRepository userRepository;
    private DepartmentRepository departmentRepository;
    private PositionRepository positionRepository;
    private UserPositionRepository userPositionRepository;
    private OrganizationQueryService organizationQueryService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserAccountRepository.class);
        departmentRepository = mock(DepartmentRepository.class);
        positionRepository = mock(PositionRepository.class);
        userPositionRepository = mock(UserPositionRepository.class);
        organizationQueryService = new OrganizationQueryService(
                userRepository,
                departmentRepository,
                mock(JobGradeRepository.class),
                positionRepository,
                userPositionRepository
        );
    }

    @Test
    void positionUsersResolveUsersForApprovalDepartmentPositionSelection() {
        UserPosition leadMapping = new UserPosition("UP1", "COM001", "lead", "DEV", "LEAD");
        leadMapping.updateMapping("COM001_DEV_LEAD_lead", "lead", "DEV", "LEAD", true);
        leadMapping.updatePrimaryYn(true);
        UserPosition financeMapping = new UserPosition("UP2", "COM001", "lead", "FIN", "LEAD");
        financeMapping.updateMapping("COM001_FIN_LEAD_lead", "lead", "FIN", "LEAD", true);
        UserAccount lead = new UserAccount("COM001", "lead", "Lead User", "hash");
        lead.updateProfile("Lead User", "lead@example.com", null, null, UserStatus.ACTIVE);
        lead.updateJobGrade("SENIOR");
        Position leadPosition = new Position("COM001", "LEAD", "Team Lead");
        leadPosition.update("Team Lead", null, 1, true);
        Department dev = department("DEV", "ROOT");
        Department finance = department("FIN", "ROOT");

        when(userPositionRepository.findByComCdAndDepartmentIdAndPositionIdAndEnabledOrderByPrimaryYnDescUserIdAsc("COM001", "DEV", "LEAD", true))
                .thenReturn(List.of(leadMapping));
        when(userRepository.findByComCdAndUserId("COM001", "lead")).thenReturn(Optional.of(lead));
        when(departmentRepository.findByComCdAndDepartmentId("COM001", "DEV")).thenReturn(Optional.of(dev));
        when(departmentRepository.findByComCdAndDepartmentId("COM001", "FIN")).thenReturn(Optional.of(finance));
        when(positionRepository.findByComCdAndPositionId("COM001", "LEAD")).thenReturn(Optional.of(leadPosition));
        when(userPositionRepository.findByComCdAndUserIdOrderByPrimaryYnDescPositionIdAsc("COM001", "lead"))
                .thenReturn(List.of(leadMapping, financeMapping));

        List<OrganizationUserResponse> result = organizationQueryService.positionUsers("COM001", "DEV", "LEAD");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("lead");
        assertThat(result.get(0).getAffiliations()).extracting("departmentId").containsExactly("DEV", "FIN");
        assertThat(result.get(0).getAffiliations()).extracting("positionId").containsExactly("LEAD", "LEAD");
    }

    private Department department(String departmentId, String parentDepartmentId) {
        Department department = new Department("COM001", departmentId, departmentId + " Name");
        department.update(parentDepartmentId, departmentId + " Name", null, null, 1, true);
        return department;
    }
}
