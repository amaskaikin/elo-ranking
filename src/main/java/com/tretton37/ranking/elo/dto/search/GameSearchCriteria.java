package com.tretton37.ranking.elo.dto.search;

import java.util.Collection;
import java.util.UUID;

public record GameSearchCriteria(Collection<UUID> playerIds, UUID winnerId, UUID tournamentId) {
}
