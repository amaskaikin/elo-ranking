package com.tretton37.ranking.elo.dto;

import com.tretton37.ranking.elo.persistence.entity.GameResult;

import java.util.Collection;
import java.util.UUID;

public record SearchCriteria(Collection<UUID> players, GameResult result) {
}
