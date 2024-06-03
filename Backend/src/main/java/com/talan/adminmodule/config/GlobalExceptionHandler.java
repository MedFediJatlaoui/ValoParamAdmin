package com.talan.adminmodule.config;

import com.talan.adminmodule.dto.ResponseDto;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
@ControllerAdvice
public class GlobalExceptionHandler {
    ResponseDto responseDto  = new ResponseDto();
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ResponseDto> handleDataAccessException(DataAccessException ex, WebRequest request) {
        String requestDetails =request.getDescription(false) ;
        LOGGER.error("Data Access error {}: {}",requestDetails, ex.getMessage(), ex);
        responseDto.setError("An error occurred while accessing data.");
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(StackOverflowError.class)
    public ResponseEntity<ResponseDto> handleDataAccessException(StackOverflowError ex, WebRequest request) {
        String requestDetails =request.getDescription(false) ;
        LOGGER.error("Data Access error {}: {}",requestDetails, ex.getMessage(), ex);
        responseDto.setError("Infinite Loop");
        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        Throwable rootCause = ex.getRootCause();
        String detailedError = (rootCause instanceof PSQLException)
                ? "PostgresSQL error: " + rootCause.getMessage()
                : "Data integrity violation: " + ex.getMessage();
String requestWithoutClientInfo =request.getDescription(false) ;
        LOGGER.error("{} - Details: {}" ,detailedError ,requestWithoutClientInfo);
        responseDto.setError("An error occurred due to data integrity issues.");
        return new ResponseEntity<>(responseDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDto> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        String requestDetails =request.getDescription(false) ;

        LOGGER.error("Invalid input {} : {}",requestDetails, ex.getMessage(), ex);
        responseDto.setError("Invalid input provided.");
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ResponseDto> handleNullPointerException(NullPointerException ex, WebRequest request) {
        String requestDetails =request.getDescription(false) ;
        LOGGER.error("Null pointer encountered {} : {}",requestDetails, ex.getMessage(), ex);
        responseDto.setError("An internal server error occurred.");
        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDto> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String requestDetails =request.getDescription(false) ;

        LOGGER.error("Unhandled exception occurred {} : {}",requestDetails, ex.getMessage(), ex);
        responseDto.setError("An unexpected error occurred. Please try again later.");
        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
