package com.tretton37.ranking.elo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Tournament(UUID uuid, @NotEmpty String name) {
}
