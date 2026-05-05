package com.upmudoum.erp.domain.warehouse.dto;

import com.upmudoum.erp.domain.warehouse.vo.WarehouseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseRequest {

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 200)
    private String location;

    private WarehouseStatus status = WarehouseStatus.ACTIVE;
}
