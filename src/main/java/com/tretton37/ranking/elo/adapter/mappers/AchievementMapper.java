package com.tretton37.ranking.elo.adapter.mappers;

import com.tretton37.ranking.elo.application.persistence.entity.AchievementEntity;
import com.tretton37.ranking.elo.application.persistence.entity.PlayerEntity;
import com.tretton37.ranking.elo.domain.model.Achievement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring")
public interface AchievementMapper {
    @Mapping(source = "players", target = "playerIds", qualifiedByName = "playerIds")
    Achievement entityToDto(AchievementEntity entity);
    @Mapping(source = ".", target = "description", qualifiedByName = "descriptionWithDefault")
    @Mapping(target = "players", ignore = true)
    AchievementEntity dtoToEntity(Achievement achievement);

    @Named("playerIds")
    default Set<UUID> playerEntitiesToIds(Set<PlayerEntity> players) {
        return Stream.ofNullable(players)
                .flatMap(Collection::stream)
                .map(PlayerEntity::getId)
                .collect(Collectors.toSet());
    }

    @Named("descriptionWithDefault")
    default String description(Achievement achievement) {
        return Optional.ofNullable(achievement.getDescription()).orElse(achievement.getType().getDescription());
    }
}
