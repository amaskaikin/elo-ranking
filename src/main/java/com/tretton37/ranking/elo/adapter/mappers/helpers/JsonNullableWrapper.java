package com.tretton37.ranking.elo.adapter.mappers.helpers;

import org.openapitools.jackson.nullable.JsonNullable;

public class JsonNullableWrapper {
    public static <T> JsonNullable<T> wrap(T entity) {
        return entity == null ? JsonNullable.undefined() : JsonNullable.of(entity);
    }

    public static <T> T unwrap(JsonNullable<T> jsonNullable) {
        return jsonNullable.orElse(null);
    }
}