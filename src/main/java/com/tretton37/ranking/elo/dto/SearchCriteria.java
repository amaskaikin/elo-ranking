package com.tretton37.ranking.elo.dto;

import java.util.Collection;
import java.util.UUID;

public record SearchCriteria(Collection<UUID> playerIds, UUID winnerId, UUID tournamentId) {
}
