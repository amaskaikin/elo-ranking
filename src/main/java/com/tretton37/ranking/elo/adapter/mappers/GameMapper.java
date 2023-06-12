package com.tretton37.ranking.elo.adapter.mappers;

import com.tretton37.ranking.elo.adapter.mappers.tournament.match.EliminationMatchMapper;
import com.tretton37.ranking.elo.application.persistence.entity.GameEntity;
import com.tretton37.ranking.elo.application.persistence.entity.tournament.TournamentMatchEntity;
import com.tretton37.ranking.elo.domain.model.Game;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import static com.tretton37.ranking.elo.domain.model.Game.TournamentDetails;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, componentModel = "spring",
        uses = {PlayerRefMapper.class, LocationMapper.class, EliminationMatchMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GameMapper {

    @Mapping(source = "gameResult.winnerId", target = "winnerId")
    @Mapping(source = "location", target = "locationRef")
    @Mapping(source = "playerA", target = "playerScoreA.playerRef")
    @Mapping(source = "gameResult.playerAScore", target = "playerScoreA.score")
    @Mapping(source = "gameResult.playerARatingAlteration", target = "playerScoreA.ratingAlteration")
    @Mapping(source = "playerB", target = "playerScoreB.playerRef")
    @Mapping(source = "gameResult.playerBScore", target = "playerScoreB.score")
    @Mapping(source = "gameResult.playerBRatingAlteration", target = "playerScoreB.ratingAlteration")
    @Mapping(source = "tournamentMatch", target = "tournamentDetails", qualifiedByName = "toTournamentDetails")
    Game entityToDto(GameEntity gameEntity);
    @Mapping(source = "locationRef", target = "location")
    @Mapping(source = "playerScoreA.playerRef", target = "playerA")
    @Mapping(source = "playerScoreB.playerRef", target = "playerB")
    @Mapping(source = "winnerId", target = "gameResult.winnerId")
    @Mapping(source = "playerScoreA.score", target = "gameResult.playerAScore")
    @Mapping(source = "playerScoreB.score", target = "gameResult.playerBScore")
    @Mapping(source = "playerScoreA.ratingAlteration", target = "gameResult.playerARatingAlteration")
    @Mapping(source = "playerScoreB.ratingAlteration", target = "gameResult.playerBRatingAlteration")
    @Mapping(source = "tournamentDetails", target = "tournamentMatch", qualifiedByName = "toTournamentMatchEntity")
    GameEntity dtoToEntity(Game game);

    @Named("toTournamentDetails")
    default TournamentDetails toTournamentDetails(TournamentMatchEntity entity) {
        if (entity == null) {
            return null;
        }
        var details = new TournamentDetails();
        details.setTournamentId(entity.getTournament().getId());
        details.setMatchId(entity.getId());

        return details;
    }

    @Named("toTournamentMatchEntity")
    default TournamentMatchEntity toTournamentMatchEntity(TournamentDetails details) {
        if (details == null || details.getMatchId() == null) {
            return null;
        }
        var entity = new TournamentMatchEntity();
        entity.setId(details.getMatchId());

        return entity;
    }
}
