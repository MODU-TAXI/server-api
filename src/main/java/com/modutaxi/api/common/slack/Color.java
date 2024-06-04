package com.modutaxi.api.common.slack;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Color {

    GREEN("#36a64f"),
    RED("#ff0000"),
    BLUE("#0000ff"),
    YELLOW("#ffff00"),
    BLACK("#000000"),
    WHITE("#ffffff");

    private final String code;
}
