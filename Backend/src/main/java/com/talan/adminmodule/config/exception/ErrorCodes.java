package com.talan.adminmodule.config.exception;

import lombok.Getter;

@Getter
public enum ErrorCodes {

    RULE_NOT_FOUND(1000),
    RULE_NOT_VALID(1001),
    ATTRIBUTE_NOT_FOUND(2000),
    ATTRIBUTE_NOT_VALID(2001) ;

    private final int code;

    ErrorCodes(int code) {
        this.code = code;
    }

}
