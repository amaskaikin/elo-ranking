package com.tretton37.ranking.elo.dto.mapper;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.persistence.entity.GameEntity;
import com.tretton37.ranking.elo.persistence.entity.PlayerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameMapper implements PersistenceMapper<Game, GameEntity> {

    private final PersistenceMapper<Player, PlayerEntity> playerMapper;

    @Autowired
    public GameMapper(PersistenceMapper<Player, PlayerEntity> playerMapper) {
        this.playerMapper = playerMapper;
    }

    @Override
    public Game entityToDto(GameEntity gameEntity) {
        return Game.builder()
                .id(gameEntity.getId())
                .playerA(playerMapper.entityToDto(gameEntity.getPlayerA()))
                .playerB(playerMapper.entityToDto(gameEntity.getPlayerB()))
                .result(gameEntity.getResult())
                .playedWhen(gameEntity.getPlayedWhen())
                .build();
    }

    @Override
    public GameEntity dtoToEntity(Game game) {
        return GameEntity.builder()
                .id(game.getId())
                .playerA(playerMapper.dtoToEntity(game.getPlayerA()))
                .playerB(playerMapper.dtoToEntity(game.getPlayerB()))
                .result(game.getResult())
                .playedWhen(game.getPlayedWhen())
                .build();
    }
}
