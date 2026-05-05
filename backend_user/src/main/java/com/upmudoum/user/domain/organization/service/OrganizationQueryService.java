package com.upmudoum.user.domain.organization.service;

import com.upmudoum.user.domain.department.Department;
import com.upmudoum.user.domain.department.DepartmentRepository;
import com.upmudoum.user.domain.jobgrade.JobGrade;
import com.upmudoum.user.domain.jobgrade.JobGradeRepository;
import com.upmudoum.user.domain.organization.dto.OrganizationDtos.OrganizationUserResponse;
import com.upmudoum.user.domain.organization.dto.OrganizationDtos.UserAffiliationResponse;
import com.upmudoum.user.domain.position.Position;
import com.upmudoum.user.domain.position.PositionRepository;
import com.upmudoum.user.domain.user.UserAccount;
import com.upmudoum.user.domain.user.UserAccountRepository;
import com.upmudoum.user.domain.user.UserStatus;
import com.upmudoum.user.domain.userposition.UserPosition;
import com.upmudoum.user.domain.userposition.UserPositionRepository;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationQueryService {

    private final UserAccountRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final JobGradeRepository jobGradeRepository;
    private final PositionRepository positionRepository;
    private final UserPositionRepository userPositionRepository;

    public OrganizationQueryService(
            UserAccountRepository userRepository,
            DepartmentRepository departmentRepository,
            JobGradeRepository jobGradeRepository,
            PositionRepository positionRepository,
            UserPositionRepository userPositionRepository
    ) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.jobGradeRepository = jobGradeRepository;
        this.positionRepository = positionRepository;
        this.userPositionRepository = userPositionRepository;
    }

    @Transactional(readOnly = true)
    public List<OrganizationUserResponse> organizationUsers(String comCd, String departmentId, String keyword) {
        List<UserAccount> users = departmentId == null || departmentId.isBlank()
                ? userRepository.findByComCdAndStatusOrderByUserNameAscUserIdAsc(comCd, UserStatus.ACTIVE)
                : userPositionRepository.findByComCdAndDepartmentIdAndEnabledOrderByPositionIdAscUserIdAsc(comCd, departmentId, true)
                .stream()
                .map(mapping -> userRepository.findByComCdAndUserId(comCd, mapping.getUserId()).orElse(null))
                .filter(user -> user != null && user.getStatus() == UserStatus.ACTIVE)
                .collect(java.util.stream.Collectors.toMap(UserAccount::getUserId, user -> user, (left, ignored) -> left, LinkedHashMap::new))
                .values()
                .stream()
                .toList();
        return users.stream()
                .filter(user -> matchesKeyword(user, keyword))
                .map(this::toOrganizationUser)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrganizationUserResponse> positionUsers(String comCd, String departmentId, String positionId) {
        return userPositionRepository.findByComCdAndDepartmentIdAndPositionIdAndEnabledOrderByPrimaryYnDescUserIdAsc(comCd, departmentId, positionId, true)
                .stream()
                .map(mapping -> userRepository.findByComCdAndUserId(comCd, mapping.getUserId()).orElse(null))
                .filter(user -> user != null && user.getStatus() == UserStatus.ACTIVE)
                .map(this::toOrganizationUser)
                .toList();
    }

    private OrganizationUserResponse toOrganizationUser(UserAccount user) {
        JobGrade jobGrade = user.getJobGradeId() == null ? null : jobGradeRepository.findByComCdAndJobGradeId(user.getComCd(), user.getJobGradeId()).orElse(null);
        List<UserAffiliationResponse> affiliations = userPositionRepository.findByComCdAndUserIdOrderByPrimaryYnDescPositionIdAsc(user.getComCd(), user.getUserId())
                .stream()
                .filter(UserPosition::isEnabled)
                .map(this::toUserAffiliation)
                .toList();
        return new OrganizationUserResponse(user.getComCd(), user.getUserId(), user.getUserName(), user.getJobGradeId(), jobGrade == null ? null : jobGrade.getJobGradeName(), affiliations);
    }

    private UserAffiliationResponse toUserAffiliation(UserPosition mapping) {
        Department department = departmentRepository.findByComCdAndDepartmentId(mapping.getComCd(), mapping.getDepartmentId()).orElse(null);
        Position position = positionRepository.findByComCdAndPositionId(mapping.getComCd(), mapping.getPositionId()).orElse(null);
        return new UserAffiliationResponse(
                mapping.getComCd(),
                mapping.getUserPositionId(),
                mapping.getDepartmentId(),
                department == null ? null : department.getDepartmentName(),
                mapping.getPositionId(),
                position == null ? null : position.getPositionName(),
                mapping.isPrimaryYn()
        );
    }

    private boolean matchesKeyword(UserAccount user, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        String normalized = keyword.toLowerCase();
        return user.getUserId().toLowerCase().contains(normalized)
                || user.getUserName().toLowerCase().contains(normalized);
    }
}
