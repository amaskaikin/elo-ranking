package com.tretton37.ranking.elo.adapter.mappers;

import com.tretton37.ranking.elo.adapter.mappers.helpers.JsonNullableWrapper;
import com.tretton37.ranking.elo.adapter.mappers.tournament.TournamentMapper;
import com.tretton37.ranking.elo.application.persistence.entity.AchievementEntity;
import com.tretton37.ranking.elo.application.persistence.entity.PlayerEntity;
import com.tretton37.ranking.elo.domain.model.Achievement;
import com.tretton37.ranking.elo.domain.model.Player;
import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// disable builder for Mapstruct as Lombok @Builder conflicts with Mapstruct @AfterMapping
@Mapper(componentModel = "spring", uses = {LocationMapper.class, TournamentMapper.class},
        builder = @Builder(disableBuilder = true))
public abstract class PlayerMapper {
    @Autowired
    protected AchievementMapper achievementMapper;

    @Mapping(source = "profileImage", target = "profileImage", qualifiedByName = "wrappedProfileImage")
    @Mapping(source = "achievements", target = "achievements", qualifiedByName = "wrappedAchievements")
    @Mapping(source = "location", target = "locationRef")
    @Mapping(target = "winRate", ignore = true)
    public abstract Player entityToDto(PlayerEntity playerEntity);

    @Mapping(source = "profileImage", target = "profileImage", qualifiedByName = "unwrappedProfileImage")
    @Mapping(source = "achievements", target = "achievements", qualifiedByName = "unwrappedAchievements")
    @Mapping(source = "locationRef", target = "location")
    @Mapping(source = "gamesPlayed", target = "gamesPlayed", defaultValue = "0")
    @Mapping(source = "gamesWon", target = "gamesWon", defaultValue = "0")
    public abstract PlayerEntity dtoToEntity(Player player);

    @Named("wrappedAchievements")
    protected JsonNullable<Collection<Achievement>> wrappedAchievements(Set<AchievementEntity> achievementEntities) {
        return JsonNullableWrapper.wrap(
                Stream.ofNullable(achievementEntities)
                        .flatMap(Collection::stream)
                        .map(achievementMapper::entityToDto)
                        .collect(Collectors.toSet())
        );
    }

    @Named("unwrappedAchievements")
    protected Set<AchievementEntity> unwrappedAchievements(JsonNullable<Collection<Achievement>> achievements) {
        return Stream.ofNullable(JsonNullableWrapper.unwrap(achievements))
                .flatMap(Collection::stream)
                .map(achievementMapper::dtoToEntity)
                .collect(Collectors.toSet());
    }

    @Named("wrappedProfileImage")
    protected JsonNullable<String> wrappedProfileImage(String profileImage) {
        return JsonNullableWrapper.wrap(profileImage);
    }

    @Named("unwrappedProfileImage")
    protected String unwrappedProfileImage(JsonNullable<String> wrapped) {
        return JsonNullableWrapper.unwrap(wrapped);
    }

    @AfterMapping
    protected void afterMappingHandler(@MappingTarget Player player) {
        if (player.getGamesPlayed() != 0) {
            player.setWinRate(BigDecimal.valueOf((double) player.getGamesWon() / player.getGamesPlayed())
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue());
        } else {
            player.setWinRate(0.0);
        }
    }
}
