package com.tretton37.ranking.elo.adapter.mappers;

import com.tretton37.ranking.elo.domain.model.Tournament;
import com.tretton37.ranking.elo.application.persistence.entity.TournamentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TournamentMapper {
    Tournament entityToDto(TournamentEntity tournamentEntity);
    TournamentEntity dtoToEntity(Tournament tournament);
}
