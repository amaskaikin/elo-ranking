package com.tretton37.ranking.elo.dto.mapper;

import com.tretton37.ranking.elo.dto.PlayerRef;
import com.tretton37.ranking.elo.persistence.entity.PlayerEntity;
import org.springframework.stereotype.Component;

@Component
public class PlayerRefMapper implements PersistenceMapper<PlayerRef, PlayerEntity> {
    @Override
    public PlayerRef entityToDto(PlayerEntity playerEntity) {
        return PlayerRef.builder()
                .id(playerEntity.getId())
                .name(playerEntity.getName())
                .rating(playerEntity.getRating())
                .build();
    }

    @Override
    public PlayerEntity dtoToEntity(PlayerRef playerRef) {
        return PlayerEntity.builder()
                .id(playerRef.getId())
                .build();
    }
}
