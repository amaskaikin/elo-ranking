package com.tretton37.ranking.elo.errorhandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ErrorHandlingController {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(EntityNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getErrorDetails(), e.getMessage()));
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(EntityAlreadyExistsException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getErrorDetails(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);

        StringBuilder messageBuilder = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(fieldError ->
                messageBuilder.append(fieldError.getField())
                        .append(": ")
                        .append(fieldError.getDefaultMessage())
                        .append("; ")
        );
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorDetails.BAD_REQUEST, messageBuilder.toString()));
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageConversionException(HttpMessageConversionException e) {
        return badRequest(e);
    }

    @ExceptionHandler(RequestConsistencyException.class)
    public ResponseEntity<ErrorResponse> handleRequestConsistencyException(RequestConsistencyException e) {
        return badRequest(e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ErrorDetails.INTERNAL_ERROR, e.getMessage()));
    }

    private ResponseEntity<ErrorResponse> badRequest(Exception e) {
        log.error(e.getMessage(), e);

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorDetails.BAD_REQUEST, e.getMessage()));
    }
}
