package com.upmudoum.user.domain.dropdown.service;

import com.upmudoum.user.domain.code.CommonCodeRepository;
import com.upmudoum.user.domain.department.DepartmentRepository;
import com.upmudoum.user.domain.dropdown.dto.DropdownOptionResponse;
import com.upmudoum.user.domain.dropdown.dto.DropdownUserOptionRow;
import com.upmudoum.user.domain.dropdown.querydsl.DropdownQueryRepository;
import com.upmudoum.user.domain.jobgrade.JobGradeRepository;
import com.upmudoum.user.domain.position.PositionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DropdownService {

    private final DepartmentRepository departmentRepository;
    private final JobGradeRepository jobGradeRepository;
    private final PositionRepository positionRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final DropdownQueryRepository dropdownQueryRepository;

    public DropdownService(
            DepartmentRepository departmentRepository,
            JobGradeRepository jobGradeRepository,
            PositionRepository positionRepository,
            CommonCodeRepository commonCodeRepository,
            DropdownQueryRepository dropdownQueryRepository
    ) {
        this.departmentRepository = departmentRepository;
        this.jobGradeRepository = jobGradeRepository;
        this.positionRepository = positionRepository;
        this.commonCodeRepository = commonCodeRepository;
        this.dropdownQueryRepository = dropdownQueryRepository;
    }

    public List<DropdownOptionResponse> getDepartments(String comCd) {
        return departmentRepository.findByComCdAndEnabledOrderBySortSeqAscDepartmentIdAsc(comCd, true).stream()
                .map(department -> new DropdownOptionResponse(department.getDepartmentName(), department.getDepartmentId()))
                .toList();
    }

    public List<DropdownOptionResponse> getJobGrades(String comCd) {
        return jobGradeRepository.findByComCdAndEnabledOrderBySortSeqAscJobGradeIdAsc(comCd, true).stream()
                .map(jobGrade -> new DropdownOptionResponse(jobGrade.getJobGradeName(), jobGrade.getJobGradeId()))
                .toList();
    }

    public List<DropdownOptionResponse> getPositions(String comCd) {
        return positionRepository.findByComCdAndEnabledOrderBySortSeqAscPositionIdAsc(comCd, true).stream()
                .map(position -> new DropdownOptionResponse(position.getPositionName(), position.getPositionId()))
                .toList();
    }

    public List<DropdownOptionResponse> getUserList(String comCd) {
        return dropdownQueryRepository.findUserDropdownRows(comCd).stream()
                .map(DropdownUserOptionRow::toOption)
                .toList();
    }

    public List<DropdownOptionResponse> getCodeList(String comCd, String serviceId, String codeGroupId) {
        return commonCodeRepository.findByComCdAndServiceIdAndCodeGroupIdAndEnabledOrderBySortOrderAsc(comCd, serviceId, codeGroupId, true).stream()
                .map(code -> new DropdownOptionResponse(code.getCodeName(), code.getCodeId()))
                .toList();
    }

}
