package com.tretton37.ranking.elo.dto.mapper;

import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.persistence.GameRepository;
import com.tretton37.ranking.elo.persistence.entity.PlayerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper implements PersistenceMapper<Player, PlayerEntity> {

    private final GameRepository gameRepository;

    @Autowired
    public PlayerMapper(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Player entityToDto(PlayerEntity playerEntity) {
        return Player.builder()
                .id(playerEntity.getId())
                .name(playerEntity.getName())
                .rating(playerEntity.getRating())
                .registeredWhen(playerEntity.getRegisteredWhen())
                .gamesPlayed(gameRepository.countByPlayer(playerEntity.getId()))
                .build();
    }

    @Override
    public PlayerEntity dtoToEntity(Player player) {
        return PlayerEntity.builder()
                .id(player.getId())
                .name(player.getName())
                .rating(player.getRating())
                .registeredWhen(player.getRegisteredWhen())
                .build();
    }
}
