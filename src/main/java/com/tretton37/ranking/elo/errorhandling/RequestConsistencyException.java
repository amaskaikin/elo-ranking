package com.tretton37.ranking.elo.errorhandling;

public class RequestConsistencyException extends RuntimeException {
    private final ErrorDetails errorDetails;

    public RequestConsistencyException(ErrorDetails errorDetails, String message) {
        super(message);
        this.errorDetails = errorDetails;
    }
}
