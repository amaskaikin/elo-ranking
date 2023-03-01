package com.tretton37.ranking.elo.service.validator;

public interface RequestValidator<Request> {
    void validate(Request request);
}
