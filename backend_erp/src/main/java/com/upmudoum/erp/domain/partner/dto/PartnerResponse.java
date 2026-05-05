package com.upmudoum.erp.domain.partner.dto;

import com.upmudoum.erp.domain.partner.entity.Partner;
import com.upmudoum.erp.domain.partner.vo.PartnerStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PartnerResponse {

    private Long id;
    private String code;
    private String name;
    private String businessNumber;
    private String partnerType;
    private PartnerStatus status;

    public static PartnerResponse from(Partner partner) {
        PartnerResponse response = new PartnerResponse();
        response.id = partner.getId();
        response.code = partner.getCode().getValue();
        response.name = partner.getName();
        response.businessNumber = partner.getBusinessNumber();
        response.partnerType = partner.getPartnerType();
        response.status = partner.getStatus();
        return response;
    }
}
