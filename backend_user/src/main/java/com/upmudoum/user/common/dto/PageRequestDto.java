package com.upmudoum.user.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PageRequestDto {

    @Min(0)
    private int page;

    @Min(1)
    @Max(200)
    private int size = 20;

    public PageRequestDto(int page, int size) {
        this.page = page;
        this.size = size == 0 ? 20 : size;
    }
}
