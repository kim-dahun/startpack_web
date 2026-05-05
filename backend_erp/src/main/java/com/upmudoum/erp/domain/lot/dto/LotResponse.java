package com.upmudoum.erp.domain.lot.dto;

import com.upmudoum.erp.domain.lot.entity.Lot;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LotResponse {

    private Long id;
    private Long itemId;
    private String itemCode;
    private String lotNo;
    private LocalDate manufacturedDate;
    private LocalDate expiredDate;
    private boolean enabled;

    public static LotResponse from(Lot lot) {
        LotResponse response = new LotResponse();
        response.id = lot.getId();
        response.itemId = lot.getItem().getId();
        response.itemCode = lot.getItem().getCode().getValue();
        response.lotNo = lot.getLotNo();
        response.manufacturedDate = lot.getManufacturedDate();
        response.expiredDate = lot.getExpiredDate();
        response.enabled = lot.isEnabled();
        return response;
    }
}
