package com.tretton37.ranking.elo.adapter.persistence.tournament.match;

import com.tretton37.ranking.elo.domain.model.tournament.TournamentMatch;

import java.util.Collection;
import java.util.UUID;

public interface TournamentMatchGateway<T extends TournamentMatch> {
    T findById(UUID id);
    Collection<T> findAllByTournamentId(UUID tournamentId);
    T save(T match);
    void saveAll(Collection<T> matches);
}