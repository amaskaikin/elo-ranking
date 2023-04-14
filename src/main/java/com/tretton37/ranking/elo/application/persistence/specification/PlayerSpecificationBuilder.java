package com.tretton37.ranking.elo.application.persistence.specification;

import com.tretton37.ranking.elo.application.persistence.entity.PlayerEntity;
import com.tretton37.ranking.elo.domain.model.search.PlayerListFilteringCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

import static org.springframework.data.jpa.domain.Specification.where;

public class PlayerSpecificationBuilder {

    private final PlayerListFilteringCriteria filteringCriteria;

    public static PlayerSpecificationBuilder forCriteria(PlayerListFilteringCriteria playerListFilteringCriteria) {
        return new PlayerSpecificationBuilder(playerListFilteringCriteria);
    }

    private PlayerSpecificationBuilder(PlayerListFilteringCriteria filteringCriteria) {
        this.filteringCriteria = filteringCriteria;
    }

    public Specification<PlayerEntity> build() {
        return where(tournamentIs(filteringCriteria.tournamentId()))
                .and(minGamesPlayed(filteringCriteria.gamesPlayed()));
    }

    private Specification<PlayerEntity> tournamentIs(final UUID tournamentId) {
        if (tournamentId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("tournament").get("id"), tournamentId);
    }

    private Specification<PlayerEntity> minGamesPlayed(final Integer value) {
        if (value == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.ge(root.get("gamesPlayed").as(Integer.class), value);
    }
}
