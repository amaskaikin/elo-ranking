package com.tretton37.ranking.elo.adapter.mappers;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.PlayerScore;
import com.tretton37.ranking.elo.domain.model.PlayerRef;
import com.tretton37.ranking.elo.domain.model.Tournament;
import com.tretton37.ranking.elo.application.persistence.entity.GameEntity;
import com.tretton37.ranking.elo.application.persistence.entity.GameResultEntity;
import com.tretton37.ranking.elo.application.persistence.entity.PlayerEntity;
import com.tretton37.ranking.elo.application.persistence.entity.TournamentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
                .playerScoreA(PlayerScore.builder()
                        .playerRef(playerRefMapper.entityToDto(gameEntity.getPlayerA()))
                        .score(gameEntity.getGameResult().getPlayerAScore())
                        .ratingAlteration(gameEntity.getGameResult().getPlayerARatingAlteration())
                        .build())
                .playerScoreB(PlayerScore.builder()
                        .playerRef(playerRefMapper.entityToDto(gameEntity.getPlayerB()))
                        .score(gameEntity.getGameResult().getPlayerBScore())
                        .ratingAlteration(gameEntity.getGameResult().getPlayerBRatingAlteration())
                        .build())
                .tournamentRef(tournamentMapper.entityToDto(gameEntity.getTournament()))
                .winnerId(gameEntity.getGameResult().getWinnerId())
                .playedWhen(gameEntity.getPlayedWhen())
                .build();
    }

    @Override
    public GameEntity dtoToEntity(Game game) {
        return GameEntity.builder()
                .id(game.getId())
                .playerA(playerRefMapper.dtoToEntity(game.getPlayerScoreA().getPlayerRef()))
                .playerB(playerRefMapper.dtoToEntity(game.getPlayerScoreB().getPlayerRef()))
                .tournament(tournamentMapper.dtoToEntity(game.getTournamentRef()))
                .gameResult(GameResultEntity.builder()
                        .winnerId(game.getWinnerId())
                        .playerAScore(game.getPlayerScoreA().getScore())
                        .playerBScore(game.getPlayerScoreB().getScore())
                        .playerARatingAlteration(game.getPlayerScoreA().getRatingAlteration())
                        .playerBRatingAlteration(game.getPlayerScoreB().getRatingAlteration())
                        .build())
                .playedWhen(game.getPlayedWhen())
                .build();
    }
}
