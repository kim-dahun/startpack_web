package com.upmudoum.user.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BulkResultDto {

    private int added;
    private int updated;
    private int deleted;
}
