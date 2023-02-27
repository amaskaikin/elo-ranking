package com.tretton37.ranking.elo.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Collection;
import java.util.UUID;

@JsonTypeName(value = "criteria")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public record SearchCriteria(Collection<UUID> playerIds, UUID winnerId, UUID tournamentId) {
}
