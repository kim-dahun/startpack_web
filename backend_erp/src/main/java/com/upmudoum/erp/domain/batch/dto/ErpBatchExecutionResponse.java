package com.upmudoum.erp.domain.batch.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.job.JobExecution;

@Getter
@NoArgsConstructor
public class ErpBatchExecutionResponse {

    private Long executionId;
    private String jobName;
    private String status;
    private String exitCode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public static ErpBatchExecutionResponse from(JobExecution execution) {
        ErpBatchExecutionResponse response = new ErpBatchExecutionResponse();
        response.executionId = execution.getId();
        response.jobName = execution.getJobInstance() == null ? null : execution.getJobInstance().getJobName();
        response.status = execution.getStatus() == null ? null : execution.getStatus().name();
        response.exitCode = execution.getExitStatus() == null ? null : execution.getExitStatus().getExitCode();
        response.startTime = execution.getStartTime();
        response.endTime = execution.getEndTime();
        return response;
    }
}
