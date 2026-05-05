package com.upmudoum.erp.domain.batch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpBatchExecutionRequest {

    @NotBlank
    @Size(max = 100)
    private String jobName;

    private Map<String, String> parameters;
}
