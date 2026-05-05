package com.upmudoum.user.domain.jobgrade.service;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.exception.BusinessException;
import com.upmudoum.user.common.exception.ErrorCode;
import com.upmudoum.user.domain.jobgrade.JobGrade;
import com.upmudoum.user.domain.jobgrade.JobGradeRepository;
import com.upmudoum.user.domain.jobgrade.JobGradeType;
import com.upmudoum.user.domain.jobgrade.dto.JobGradeDtos.JobGradeRequest;
import com.upmudoum.user.domain.jobgrade.dto.JobGradeDtos.JobGradeResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobGradeManagementService {

    private final JobGradeRepository jobGradeRepository;

    public JobGradeManagementService(JobGradeRepository jobGradeRepository) {
        this.jobGradeRepository = jobGradeRepository;
    }

    @Transactional(readOnly = true)
    public List<JobGradeResponse> jobGrades(String comCd) {
        return jobGradeRepository.findByComCdOrderBySortSeqAscJobGradeIdAsc(comCd).stream().map(this::toJobGrade).toList();
    }

    @Transactional
    public BulkResultDto saveJobGrades(BulkRequestDto<JobGradeRequest> request) {
        request.getAdded().forEach(item -> {
            JobGrade jobGrade = new JobGrade(item.getComCd(), item.getJobGradeId(), item.getJobGradeName());
            jobGrade.update(item.getJobGradeName(), toJobGradeType(item.getJobGradeType()), item.getSortSeq(), item.isEnabled());
            jobGradeRepository.save(jobGrade);
        });
        request.getUpdated().forEach(item -> jobGradeRepository.findByComCdAndJobGradeId(item.getComCd(), item.getJobGradeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "JobGrade was not found."))
                .update(item.getJobGradeName(), toJobGradeType(item.getJobGradeType()), item.getSortSeq(), item.isEnabled()));
        request.getDeleted().forEach(item -> jobGradeRepository.deleteByComCdAndJobGradeId(item.getComCd(), item.getJobGradeId()));
        return new BulkResultDto(request.getAdded().size(), request.getUpdated().size(), request.getDeleted().size());
    }

    private JobGradeType toJobGradeType(String value) {
        if (value == null || value.isBlank()) {
            return JobGradeType.CUSTOM;
        }
        try {
            return JobGradeType.valueOf(value.toUpperCase());
        } catch (RuntimeException ex) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Unsupported jobGradeType.");
        }
    }

    private JobGradeResponse toJobGrade(JobGrade jobGrade) {
        return new JobGradeResponse(jobGrade.getComCd(), jobGrade.getJobGradeId(), jobGrade.getJobGradeName(), jobGrade.getJobGradeType().name(), jobGrade.getSortSeq(), jobGrade.isEnabled());
    }
}
