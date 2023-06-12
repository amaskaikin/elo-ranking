package com.tretton37.ranking.elo.adapter.mappers.tournament;

import com.tretton37.ranking.elo.adapter.mappers.LocationMapper;
import com.tretton37.ranking.elo.application.persistence.entity.tournament.TournamentEntity;
import com.tretton37.ranking.elo.domain.model.tournament.Tournament;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {LocationMapper.class})
public interface TournamentMapper {
    Tournament toDto(TournamentEntity tournamentEntity);
    TournamentEntity toEntity(Tournament tournament);
}
