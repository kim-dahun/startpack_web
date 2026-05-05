package com.upmudoum.erp.domain.batch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpBatchDefinitionRequest {

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 200)
    private String description;

    @NotBlank
    @Size(max = 50)
    private String triggerPolicy;

    private boolean required;
}
