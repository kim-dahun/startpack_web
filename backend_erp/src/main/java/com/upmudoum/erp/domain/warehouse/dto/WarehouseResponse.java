package com.upmudoum.erp.domain.warehouse.dto;

import com.upmudoum.erp.domain.warehouse.entity.Warehouse;
import com.upmudoum.erp.domain.warehouse.vo.WarehouseStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WarehouseResponse {

    private Long id;
    private String code;
    private String name;
    private String location;
    private WarehouseStatus status;

    public static WarehouseResponse from(Warehouse warehouse) {
        WarehouseResponse response = new WarehouseResponse();
        response.id = warehouse.getId();
        response.code = warehouse.getCode().getValue();
        response.name = warehouse.getName();
        response.location = warehouse.getLocation();
        response.status = warehouse.getStatus();
        return response;
    }
}
