package com.tretton37.ranking.elo.domain.model.search;

import com.tretton37.ranking.elo.domain.model.GameStatus;

import java.util.Collection;
import java.util.UUID;

public record GameSearchCriteria(Collection<UUID> playerIds, UUID winnerId, UUID tournamentId, GameStatus status) {
}
