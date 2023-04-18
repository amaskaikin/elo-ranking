package com.tretton37.ranking.elo.adapter.mappers;

import com.tretton37.ranking.elo.application.persistence.entity.AchievementEntity;
import com.tretton37.ranking.elo.application.persistence.entity.PlayerEntity;
import com.tretton37.ranking.elo.domain.model.Achievement;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AchievementMapper implements PersistenceMapper<Achievement, AchievementEntity> {
    @Override
    public Achievement entityToDto(AchievementEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Achievement(entity.getId(), entity.getType(),
                entity.getName(), entity.getDescription(), entity.getIcon(),
                Stream.ofNullable(entity.getPlayers())
                        .flatMap(Collection::stream)
                        .map(PlayerEntity::getId)
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public AchievementEntity dtoToEntity(Achievement achievement) {
        if (achievement == null) {
            return null;
        }
        return new AchievementEntity(achievement.getId(), achievement.getType(), achievement.getName(),
                Optional.ofNullable(achievement.getDescription())
                        .orElse(achievement.getType().getDescription()),
                achievement.getIcon());
    }
}
