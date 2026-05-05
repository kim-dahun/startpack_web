package com.upmudoum.erp.domain.route.dto;

import com.upmudoum.erp.domain.route.entity.Route;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RouteResponse {

    private Long id;
    private String code;
    private String name;
    private Long itemId;
    private String itemCode;
    private boolean enabled;

    public static RouteResponse from(Route route) {
        RouteResponse response = new RouteResponse();
        response.id = route.getId();
        response.code = route.getCode();
        response.name = route.getName();
        response.itemId = route.getItem() == null ? null : route.getItem().getId();
        response.itemCode = route.getItem() == null ? null : route.getItem().getCode().getValue();
        response.enabled = route.isEnabled();
        return response;
    }
}
