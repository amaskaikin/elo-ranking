package com.tretton37.ranking.elo.dto.mapper;

import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

@Component
public class JsonNullableMapper {

    public  <T> JsonNullable<T> wrap(T entity) {
        return entity == null ? JsonNullable.undefined() : JsonNullable.of(entity);
    }

    public <T> T unwrap(JsonNullable<T> jsonNullable) {
        return jsonNullable.orElse(null);
    }
}