package com.tretton37.ranking.elo.adapter.mappers;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.GameStatus;
import com.tretton37.ranking.elo.domain.model.PlayerRef;
import com.tretton37.ranking.elo.domain.model.Tournament;
import com.tretton37.ranking.elo.application.persistence.entity.GameEntity;
import com.tretton37.ranking.elo.application.persistence.entity.GameResultEntity;
import com.tretton37.ranking.elo.application.persistence.entity.PlayerEntity;
import com.tretton37.ranking.elo.application.persistence.entity.TournamentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GameMapper implements PersistenceMapper<Game, GameEntity> {

    private final PersistenceMapper<PlayerRef, PlayerEntity> playerRefMapper;
    private final PersistenceMapper<Tournament, TournamentEntity> tournamentMapper;

    @Autowired
    public GameMapper(PersistenceMapper<PlayerRef, PlayerEntity> playerRefMapper,
                      PersistenceMapper<Tournament, TournamentEntity> tournamentMapper) {
        this.playerRefMapper = playerRefMapper;
        this.tournamentMapper = tournamentMapper;
    }

    @Override
    public Game entityToDto(GameEntity gameEntity) {
        return Game.builder()
                .id(gameEntity.getId())
                .playerRefA(playerRefMapper.entityToDto(gameEntity.getPlayerA()))
                .playerRefB(playerRefMapper.entityToDto(gameEntity.getPlayerB()))
                .tournamentRef(tournamentMapper.entityToDto(gameEntity.getTournament()))
                .gameResult(Game.GameResult.builder()
                        .winnerId(gameEntity.getGameResult().getWinnerId())
                        .playerAScore(gameEntity.getGameResult().getPlayerAScore())
                        .playerBScore(gameEntity.getGameResult().getPlayerBScore())
                        .playerARatingAlteration(gameEntity.getGameResult().getPlayerARatingAlteration())
                        .playerBRatingAlteration(gameEntity.getGameResult().getPlayerBRatingAlteration())
                        .build())
                .status(GameStatus.valueOf(gameEntity.getStatus()))
                .playedWhen(gameEntity.getPlayedWhen())
                .build();
    }

    @Override
    public GameEntity dtoToEntity(Game game) {
        return GameEntity.builder()
                .id(game.getId())
                .playerA(playerRefMapper.dtoToEntity(game.getPlayerRefA()))
                .playerB(playerRefMapper.dtoToEntity(game.getPlayerRefB()))
                .tournament(tournamentMapper.dtoToEntity(game.getTournamentRef()))
                .gameResult(GameResultEntity.builder()
                        .winnerId(game.getGameResult().getWinnerId())
                        .playerAScore(game.getGameResult().getPlayerAScore())
                        .playerBScore(game.getGameResult().getPlayerBScore())
                        .playerARatingAlteration(game.getGameResult().getPlayerARatingAlteration())
                        .playerBRatingAlteration(game.getGameResult().getPlayerBRatingAlteration())
                        .build())
                .status(Optional.ofNullable(game.getStatus()).orElse(GameStatus.COMPLETED).name())
                .playedWhen(game.getPlayedWhen())
                .build();
    }
}
