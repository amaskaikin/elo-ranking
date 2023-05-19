package com.tretton37.ranking.elo.domain.model.search;

import java.util.UUID;

public record PlayerFilteringCriteria(UUID locationId, Integer gamesPlayed, String email, String name) {
}
