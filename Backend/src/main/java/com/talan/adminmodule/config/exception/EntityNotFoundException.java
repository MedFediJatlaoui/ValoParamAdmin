package com.talan.adminmodule.config.exception;


import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

    private final ErrorCodes errorCode;


    public EntityNotFoundException(String message, ErrorCodes errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
