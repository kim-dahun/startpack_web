package com.upmudoum.erp.domain.batch.config;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ErpClosingBatchConfig {

    @Bean
    public Job erpMonthlyClosingJob(JobRepository jobRepository, Step erpMonthlyClosingStep) {
        return new JobBuilder("erpMonthlyClosingJob", jobRepository)
                .start(erpMonthlyClosingStep)
                .build();
    }

    @Bean
    public Step erpMonthlyClosingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("erpMonthlyClosingStep", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED, transactionManager)
                .build();
    }

    @Bean
    public Job erpInventoryClosingJob(JobRepository jobRepository, Step erpInventoryClosingStep) {
        return new JobBuilder("erpInventoryClosingJob", jobRepository)
                .start(erpInventoryClosingStep)
                .build();
    }

    @Bean
    public Step erpInventoryClosingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("erpInventoryClosingStep", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED, transactionManager)
                .build();
    }

    @Bean
    public Job inventorySummaryJob(JobRepository jobRepository, Step inventorySummaryStep) {
        return new JobBuilder("inventorySummaryJob", jobRepository)
                .start(inventorySummaryStep)
                .build();
    }

    @Bean
    public Step inventorySummaryStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("inventorySummaryStep", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED, transactionManager)
                .build();
    }
}
