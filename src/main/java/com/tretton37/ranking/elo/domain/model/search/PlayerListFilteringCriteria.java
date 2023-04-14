package com.tretton37.ranking.elo.domain.model.search;

import java.util.UUID;

public record PlayerListFilteringCriteria(UUID tournamentId, Integer gamesPlayed) {
}
