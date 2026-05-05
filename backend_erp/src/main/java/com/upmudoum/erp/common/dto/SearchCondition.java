package com.upmudoum.erp.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchCondition {

    private String keyword;

    @Min(0)
    private int page = 0;

    @Min(1)
    @Max(100)
    private int size = 20;

}
