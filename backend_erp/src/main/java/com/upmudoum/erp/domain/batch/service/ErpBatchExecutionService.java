package com.upmudoum.erp.domain.batch.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.batch.dto.ErpBatchExecutionRequest;
import com.upmudoum.erp.domain.batch.dto.ErpBatchExecutionResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.explore.JobExplorer;
import org.springframework.stereotype.Service;

@Service
public class ErpBatchExecutionService {

    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    private final Map<String, Job> jobs;

    public ErpBatchExecutionService(JobLauncher jobLauncher, JobExplorer jobExplorer, Map<String, Job> jobs) {
        this.jobLauncher = jobLauncher;
        this.jobExplorer = jobExplorer;
        this.jobs = jobs;
    }

    public ErpBatchExecutionResponse run(ErpBatchExecutionRequest request) {
        Job job = jobs.get(request.getJobName());
        if (job == null) {
            throw new BusinessException("Batch job not found");
        }
        try {
            JobParametersBuilder builder = new JobParametersBuilder()
                    .addLocalDateTime("requestedAt", LocalDateTime.now());
            if (request.getParameters() != null) {
                request.getParameters().forEach(builder::addString);
            }
            return ErpBatchExecutionResponse.from(jobLauncher.run(job, builder.toJobParameters()));
        } catch (Exception exception) {
            throw new BusinessException("Batch job execution failed: " + exception.getMessage());
        }
    }

    public List<ErpBatchExecutionResponse> findExecutions(String jobName) {
        return jobExplorer.findJobInstancesByJobName(jobName, 0, 20).stream()
                .flatMap(instance -> jobExplorer.getJobExecutions(instance).stream())
                .map(ErpBatchExecutionResponse::from)
                .toList();
    }

    public ErpBatchExecutionResponse findExecution(Long executionId) {
        JobExecution execution = jobExplorer.getJobExecution(executionId);
        if (execution == null) {
            throw new BusinessException("Batch execution not found");
        }
        return ErpBatchExecutionResponse.from(execution);
    }
}
