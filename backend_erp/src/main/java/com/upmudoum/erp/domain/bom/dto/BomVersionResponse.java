package com.upmudoum.erp.domain.bom.dto;

import com.upmudoum.erp.domain.bom.entity.BomVersion;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BomVersionResponse {

    private Long id;
    private Long bomId;
    private Long parentItemId;
    private String parentItemCode;
    private String versionNo;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private boolean enabled;
    private boolean defaultVersion;

    public static BomVersionResponse from(BomVersion bomVersion) {
        BomVersionResponse response = new BomVersionResponse();
        response.id = bomVersion.getId();
        response.bomId = bomVersion.getBom().getId();
        response.parentItemId = bomVersion.getBom().getParentItem().getId();
        response.parentItemCode = bomVersion.getBom().getParentItem().getCode().getValue();
        response.versionNo = bomVersion.getVersionNo();
        response.effectiveFrom = bomVersion.getEffectiveFrom();
        response.effectiveTo = bomVersion.getEffectiveTo();
        response.enabled = bomVersion.isEnabled();
        response.defaultVersion = bomVersion.getBom().getDefaultBomVersion() != null
                && bomVersion.getBom().getDefaultBomVersion().getId().equals(bomVersion.getId());
        return response;
    }
}
