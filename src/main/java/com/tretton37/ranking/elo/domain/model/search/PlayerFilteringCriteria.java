package com.tretton37.ranking.elo.domain.model.search;

import java.util.UUID;

public record PlayerFilteringCriteria(UUID tournamentId, Integer gamesPlayed, String email, String name) {
}
