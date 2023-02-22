package com.tretton37.ranking.elo.errorhandling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorDetails {
    INTERNAL_ERROR("ELO-001", "Internal Server error"),
    ENTITY_NOT_FOUND("ELO-002", "Requested Entity not found"),
    ENTITY_ALREADY_EXISTS("ELO-003", "Entity already exists"),
    BAD_REQUEST("ELO-004", "Bad Request");

    private final String code;
    private final String message;
}
