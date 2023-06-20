package com.tretton37.ranking.elo.adapter.mappers.tournament.match;

import com.tretton37.ranking.elo.application.persistence.entity.tournament.TournamentMatchEntity;

public interface TournamentMatchMapper<T> {
    T toDto(TournamentMatchEntity matchEntity);
    TournamentMatchEntity toEntity(T match);
}
