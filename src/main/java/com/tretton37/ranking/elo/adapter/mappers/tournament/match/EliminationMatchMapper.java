package com.tretton37.ranking.elo.adapter.mappers.tournament.match;

import com.tretton37.ranking.elo.adapter.mappers.PlayerRefMapper;
import com.tretton37.ranking.elo.adapter.mappers.tournament.RelationEntitiesQualifier;
import com.tretton37.ranking.elo.adapter.mappers.tournament.TournamentMapper;
import com.tretton37.ranking.elo.application.persistence.entity.tournament.TournamentMatchEntity;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.EliminationMatch;
import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PlayerRefMapper.class,
        TournamentMapper.class, RelationEntitiesQualifier.class})
public interface EliminationMatchMapper extends TournamentMatchMapper<EliminationMatch> {
    @Mapping(target = "previousPair", expression = "java(createPair(matchEntity.getPreviousMatchA(), " +
            "matchEntity.getPreviousMatchB()))")
    @Mapping(target = "winnerId", source = "winner.id")
    @Mapping(target = "tournamentId", source = "tournament.id")
    @Mapping(source = "playedGames", target = "playedGamesIds", qualifiedByName = "gameEntitiesToIds")
    @Mapping(target = "previousPairIds", ignore = true)
    EliminationMatch toDto(TournamentMatchEntity matchEntity);

    @Mapping(target = "previousMatchA", expression = "java(toEntity(match.getPreviousPair() == null ? null " +
            ": match.getPreviousPair().getLeft()))")
    @Mapping(target = "previousMatchB", expression = "java(toEntity(match.getPreviousPair() == null ? null " +
            ": match.getPreviousPair().getRight()))")
    @Mapping(source = "winnerId", target = "winner", qualifiedByName = "winnerId")
    @Mapping(source = "tournamentId", target = "tournament", qualifiedByName = "tournamentId")
    @Mapping(source = "playedGamesIds", target = "playedGames", qualifiedByName = "gameIdsToEntities")
    @Mapping(target = "group", ignore = true)
    @InheritInverseConfiguration
    TournamentMatchEntity toEntity(EliminationMatch match);

    default Pair<EliminationMatch, EliminationMatch> createPair(TournamentMatchEntity previousMatchA,
                                                                TournamentMatchEntity previousMatchB) {
        return Pair.of(toDto(previousMatchA), toDto(previousMatchB));
    }
}
