package com.tretton37.ranking.elo.dto.search;

import java.util.UUID;

public record PlayerSearchCriteria(String name, UUID tournamentId) {
}
