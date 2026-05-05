package com.upmudoum.erp.domain.process.dto;

import com.upmudoum.erp.domain.process.entity.ErpProcess;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErpProcessResponse {

    private Long id;
    private String code;
    private String name;
    private String processType;
    private String description;
    private boolean enabled;

    public static ErpProcessResponse from(ErpProcess process) {
        ErpProcessResponse response = new ErpProcessResponse();
        response.id = process.getId();
        response.code = process.getCode();
        response.name = process.getName();
        response.processType = process.getProcessType();
        response.description = process.getDescription();
        response.enabled = process.isEnabled();
        return response;
    }
}
