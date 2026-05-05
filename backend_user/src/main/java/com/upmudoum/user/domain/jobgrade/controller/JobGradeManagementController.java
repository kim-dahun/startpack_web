package com.upmudoum.user.domain.jobgrade.controller;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.response.ApiResponse;
import com.upmudoum.user.domain.jobgrade.dto.JobGradeDtos.JobGradeRequest;
import com.upmudoum.user.domain.jobgrade.dto.JobGradeDtos.JobGradeResponse;
import com.upmudoum.user.domain.jobgrade.service.JobGradeManagementService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/job-grades")
public class JobGradeManagementController {

    private final JobGradeManagementService jobGradeManagementService;

    public JobGradeManagementController(JobGradeManagementService jobGradeManagementService) {
        this.jobGradeManagementService = jobGradeManagementService;
    }

    @GetMapping
    public ApiResponse<List<JobGradeResponse>> jobGrades(@RequestParam String comCd) {
        return ApiResponse.ok(jobGradeManagementService.jobGrades(comCd));
    }

    @PostMapping("/bulk")
    public ApiResponse<BulkResultDto> saveJobGrades(@RequestBody BulkRequestDto<JobGradeRequest> request) {
        return ApiResponse.ok(jobGradeManagementService.saveJobGrades(request));
    }
}
