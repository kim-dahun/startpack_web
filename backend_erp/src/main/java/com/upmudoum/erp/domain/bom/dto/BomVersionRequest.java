package com.upmudoum.erp.domain.bom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BomVersionRequest {

    @NotNull
    private Long parentItemId;

    @NotBlank
    @Size(max = 30)
    private String versionNo;

    @NotNull
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;
}
