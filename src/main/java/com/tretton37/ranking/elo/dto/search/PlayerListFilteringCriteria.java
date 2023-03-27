package com.tretton37.ranking.elo.dto.search;

import java.util.UUID;

public record PlayerListFilteringCriteria(UUID tournamentId, Integer gamesPlayed) {
}
