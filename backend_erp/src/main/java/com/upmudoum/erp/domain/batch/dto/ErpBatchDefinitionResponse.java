package com.upmudoum.erp.domain.batch.dto;

import com.upmudoum.erp.domain.batch.entity.ErpBatchDefinition;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErpBatchDefinitionResponse {

    private Long id;
    private String code;
    private String description;
    private String triggerPolicy;
    private boolean required;

    public static ErpBatchDefinitionResponse from(ErpBatchDefinition definition) {
        ErpBatchDefinitionResponse response = new ErpBatchDefinitionResponse();
        response.id = definition.getId();
        response.code = definition.getCode();
        response.description = definition.getDescription();
        response.triggerPolicy = definition.getTriggerPolicy();
        response.required = definition.isRequired();
        return response;
    }
}
