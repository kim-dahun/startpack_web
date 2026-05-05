package com.upmudoum.user.domain.dropdown.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DropdownOptionResponse {

    private String label;
    private String value;
    private String label2;
    private String value2;

    public DropdownOptionResponse(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
