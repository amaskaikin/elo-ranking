package com.tretton37.ranking.elo.adapter.mappers;

import com.tretton37.ranking.elo.domain.model.Tournament;
import com.tretton37.ranking.elo.application.persistence.entity.TournamentEntity;
import org.springframework.stereotype.Component;

@Component
public class TournamentMapper implements PersistenceMapper<Tournament, TournamentEntity> {
    @Override
    public Tournament entityToDto(TournamentEntity tournamentEntity) {
        if (tournamentEntity == null) {
            return null;
        }
        return new Tournament(tournamentEntity.getId(), tournamentEntity.getName());
    }

    @Override
    public TournamentEntity dtoToEntity(Tournament tournament) {
        if (tournament == null) {
            return null;
        }
        return new TournamentEntity(tournament.getId(), tournament.getName());
    }
}