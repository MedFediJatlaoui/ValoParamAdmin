package com.talan.adminmodule.config.exception;

import lombok.Getter;

@Getter
public class InvalidOperationException extends RuntimeException {

    private final ErrorCodes errorCode;



    public InvalidOperationException(String message, ErrorCodes errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
