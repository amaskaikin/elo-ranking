package com.tretton37.ranking.elo.adapter.mappers;

import com.tretton37.ranking.elo.application.persistence.entity.GameEntity;
import com.tretton37.ranking.elo.domain.model.Game;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, componentModel = "spring",
        uses = {PlayerRefMapper.class, LocationMapper.class})
public abstract class GameMapper {

    @Mapping(source = "gameResult.winnerId", target = "winnerId")
    @Mapping(source = "location", target = "locationRef")
    @Mapping(source = "playerA", target = "playerScoreA.playerRef")
    @Mapping(source = "gameResult.playerAScore", target = "playerScoreA.score")
    @Mapping(source = "gameResult.playerARatingAlteration", target = "playerScoreA.ratingAlteration")
    @Mapping(source = "playerB", target = "playerScoreB.playerRef")
    @Mapping(source = "gameResult.playerBScore", target = "playerScoreB.score")
    @Mapping(source = "gameResult.playerBRatingAlteration", target = "playerScoreB.ratingAlteration")
    public abstract Game entityToDto(GameEntity gameEntity);
    @Mapping(source = "locationRef", target = "location")
    @Mapping(source = "playerScoreA.playerRef", target = "playerA")
    @Mapping(source = "playerScoreB.playerRef", target = "playerB")
    @Mapping(source = "winnerId", target = "gameResult.winnerId")
    @Mapping(source = "playerScoreA.score", target = "gameResult.playerAScore")
    @Mapping(source = "playerScoreB.score", target = "gameResult.playerBScore")
    @Mapping(source = "playerScoreA.ratingAlteration", target = "gameResult.playerARatingAlteration")
    @Mapping(source = "playerScoreB.ratingAlteration", target = "gameResult.playerBRatingAlteration")
    public abstract GameEntity dtoToEntity(Game game);
}
