package com.tretton37.ranking.elo.domain.model.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EntityAlreadyExistsException extends RuntimeException {
    private final ErrorDetails errorDetails;

    public EntityAlreadyExistsException(ErrorDetails errorDetails, String message) {
        super(message);
        this.errorDetails = errorDetails;
    }
}