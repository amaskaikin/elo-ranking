package com.tretton37.ranking.elo.application.persistence.specification;

import com.tretton37.ranking.elo.domain.model.search.GameSearchCriteria;
import com.tretton37.ranking.elo.application.persistence.entity.GameEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.allOf;
import static org.springframework.data.jpa.domain.Specification.where;

public class GameSpecificationBuilder {
    private final GameSearchCriteria gameSearchCriteria;

    public static GameSpecificationBuilder forCriteria(GameSearchCriteria gameSearchCriteria) {
        return new GameSpecificationBuilder(gameSearchCriteria);
    }

    private GameSpecificationBuilder(GameSearchCriteria gameSearchCriteria) {
        this.gameSearchCriteria = gameSearchCriteria;
    }

    public Specification<GameEntity> build() {
        return where(allOf(Objects.requireNonNullElse(gameSearchCriteria.playerIds(), Collections.<UUID>emptyList())
                        .stream()
                        .map(this::playerIs)
                        .collect(Collectors.toSet())
                )
                .and(winnerIs(gameSearchCriteria.winnerId()))
                .and(locationIs(gameSearchCriteria.locationId()))
        );
    }

    private Specification<GameEntity> playerIs(UUID id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.equal(root.get("playerA").get("id"), id),
                criteriaBuilder.equal(root.get("playerB").get("id"), id)
        );
    }

    private Specification<GameEntity> winnerIs(UUID winnerId) {
        if (winnerId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.join("gameResult")
                        .get("winnerId"), winnerId);
    }

    private Specification<GameEntity> locationIs(UUID locationId) {
        if (locationId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("location").get("id"), locationId);
    }
}
