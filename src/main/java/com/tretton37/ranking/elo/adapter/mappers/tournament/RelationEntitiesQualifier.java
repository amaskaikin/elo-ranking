package com.tretton37.ranking.elo.adapter.mappers.tournament;

import com.tretton37.ranking.elo.application.persistence.entity.GameEntity;
import com.tretton37.ranking.elo.application.persistence.entity.PlayerEntity;
import com.tretton37.ranking.elo.application.persistence.entity.tournament.TournamentEntity;
import com.tretton37.ranking.elo.application.persistence.entity.tournament.group.GroupEntity;
import com.tretton37.ranking.elo.application.persistence.repository.GameRepository;
import com.tretton37.ranking.elo.application.persistence.repository.PlayerRepository;
import com.tretton37.ranking.elo.application.persistence.repository.tournament.GroupRepository;
import com.tretton37.ranking.elo.application.persistence.repository.tournament.TournamentRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Mapper(componentModel = "spring")
public class RelationEntitiesQualifier {
    @Autowired
    protected PlayerRepository playerRepository;
    @Autowired
    protected GameRepository gameRepository;
    @Autowired
    protected TournamentRepository tournamentRepository;
    @Autowired
    protected GroupRepository groupRepository;

    @Named("gameEntitiesToIds")
    public Collection<UUID> gameEntitiesToIds(Collection<GameEntity> players) {
        return Stream.ofNullable(players)
                .flatMap(Collection::stream)
                .map(GameEntity::getId)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Named("winnerId")
    public PlayerEntity winnerId(UUID winnerId) {
        return Optional.ofNullable(winnerId)
                .map(playerRepository::getReferenceById)
                .orElse(null);
    }

    @Named("tournamentId")
    public TournamentEntity tournamentId(UUID tournamentId) {
        return Optional.ofNullable(tournamentId)
                .map(tournamentRepository::getReferenceById)
                .orElse(null);
    }

    @Named("groupId")
    public GroupEntity groupId(UUID groupId) {
        return Optional.ofNullable(groupId)
                .map(groupRepository::getReferenceById)
                .orElse(null);
    }

    @Named("gameIdsToEntities")
    public Collection<GameEntity> gameIdsToEntities(Collection<UUID> players) {
        return Stream.ofNullable(players)
                .flatMap(Collection::stream)
                .map(gameRepository::getReferenceById)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
