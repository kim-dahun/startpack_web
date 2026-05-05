package com.upmudoum.groupware.common.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageSlice<T> {

    private List<T> items;
    private int page;
    private int size;
    private boolean hasNext;
}
