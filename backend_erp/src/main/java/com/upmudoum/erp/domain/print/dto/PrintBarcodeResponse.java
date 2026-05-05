package com.upmudoum.erp.domain.print.dto;

import com.upmudoum.erp.domain.print.entity.PrintBarcode;
import com.upmudoum.erp.domain.print.vo.PrintDocumentType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PrintBarcodeResponse {

    private Long id;
    private String barcodeValue;
    private PrintDocumentType documentType;
    private String documentKey;
    private boolean enabled;

    public static PrintBarcodeResponse from(PrintBarcode barcode) {
        PrintBarcodeResponse response = new PrintBarcodeResponse();
        response.id = barcode.getId();
        response.barcodeValue = barcode.getBarcodeValue();
        response.documentType = barcode.getDocumentType();
        response.documentKey = barcode.getDocumentKey();
        response.enabled = barcode.isEnabled();
        return response;
    }
}
