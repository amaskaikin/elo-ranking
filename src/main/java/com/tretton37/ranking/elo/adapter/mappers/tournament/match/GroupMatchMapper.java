package com.tretton37.ranking.elo.adapter.mappers.tournament.match;

import com.tretton37.ranking.elo.adapter.mappers.PlayerRefMapper;
import com.tretton37.ranking.elo.adapter.mappers.tournament.RelationEntitiesQualifier;
import com.tretton37.ranking.elo.adapter.mappers.tournament.TournamentMapper;
import com.tretton37.ranking.elo.application.persistence.entity.tournament.TournamentMatchEntity;
import com.tretton37.ranking.elo.domain.model.tournament.group.GroupMatch;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PlayerRefMapper.class,
        TournamentMapper.class, RelationEntitiesQualifier.class})
public interface GroupMatchMapper extends TournamentMatchMapper<GroupMatch> {

    @Mapping(target = "winnerId", source = "winner.id")
    @Mapping(target = "tournamentId", source = "tournament.id")
    @Mapping(target = "groupId", source = "group.id")
    @Mapping(source = "playedGames", target = "playedGamesIds", qualifiedByName = "gameEntitiesToIds")
    GroupMatch toDto(TournamentMatchEntity matchEntity);

    @Mapping(source = "winnerId", target = "winner", qualifiedByName = "winnerId")
    @Mapping(source = "tournamentId", target = "tournament", qualifiedByName = "tournamentId")
    @Mapping(source = "groupId", target = "group", qualifiedByName = "groupId")
    @Mapping(source = "playedGamesIds", target = "playedGames", qualifiedByName = "gameIdsToEntities")
    @Mapping(target = "previousMatchA", ignore = true)
    @Mapping(target = "previousMatchB", ignore = true)
    @Mapping(target = "playerAScore", ignore = true)
    @Mapping(target = "playerBScore", ignore = true)
    @Mapping(target = "round", ignore = true)
    @InheritInverseConfiguration
    TournamentMatchEntity toEntity(GroupMatch match);
}

