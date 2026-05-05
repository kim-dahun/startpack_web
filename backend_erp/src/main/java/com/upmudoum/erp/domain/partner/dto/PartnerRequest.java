package com.upmudoum.erp.domain.partner.dto;

import com.upmudoum.erp.domain.partner.vo.PartnerStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerRequest {

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 30)
    private String businessNumber;

    @NotBlank
    @Size(max = 20)
    private String partnerType;

    private PartnerStatus status = PartnerStatus.ACTIVE;
}
