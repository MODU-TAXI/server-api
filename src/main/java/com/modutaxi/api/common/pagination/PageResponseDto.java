package com.modutaxi.api.common.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class PageResponseDto<T> {

    private int page;
    private boolean hasNext;
    private T result;
}
