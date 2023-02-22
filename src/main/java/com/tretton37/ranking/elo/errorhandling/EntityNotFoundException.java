package com.tretton37.ranking.elo.errorhandling;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EntityNotFoundException extends RuntimeException {
    private final ErrorDetails errorDetails;

    public EntityNotFoundException(ErrorDetails errorDetails, String message) {
        super(message);
        this.errorDetails = errorDetails;
    }
}