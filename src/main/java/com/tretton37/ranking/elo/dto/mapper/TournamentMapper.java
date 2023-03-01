package com.tretton37.ranking.elo.dto.mapper;

import com.tretton37.ranking.elo.dto.Tournament;
import com.tretton37.ranking.elo.persistence.entity.TournamentEntity;
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
        return new TournamentEntity(tournament.id(), tournament.name());
    }
}
