package com.upmudoum.trade.domain.master.dto;

import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TradeMasterImportRequest {

    @NotNull
    private TradeMasterType masterType;

    @NotBlank
    private String sourceFileName;

    private String sourceVersion;

    @Valid
    @NotEmpty
    private List<TradeMasterImportRowDto> rows;
}
