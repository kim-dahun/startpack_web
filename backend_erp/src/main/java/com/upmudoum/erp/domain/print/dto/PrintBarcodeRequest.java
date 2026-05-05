package com.upmudoum.erp.domain.print.dto;

import com.upmudoum.erp.domain.print.vo.PrintDocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrintBarcodeRequest {

    @NotBlank
    @Size(max = 120)
    private String barcodeValue;

    @NotNull
    private PrintDocumentType documentType;

    @NotBlank
    @Size(max = 120)
    private String documentKey;

    private boolean enabled = true;
}
