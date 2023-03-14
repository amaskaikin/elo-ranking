package com.tretton37.ranking.elo.service.player;

import com.tretton37.ranking.elo.dto.search.PlayerSearchCriteria;
import com.tretton37.ranking.elo.persistence.entity.PlayerEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

import static org.springframework.data.jpa.domain.Specification.where;

public class PlayerSpecificationBuilder {

    private final PlayerSearchCriteria searchCriteria;

    public static PlayerSpecificationBuilder forCriteria(PlayerSearchCriteria playerSearchCriteria) {
        return new PlayerSpecificationBuilder(playerSearchCriteria);
    }

    private PlayerSpecificationBuilder(PlayerSearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public Specification<PlayerEntity> build() {
        return where(tournamentIs(searchCriteria.tournamentId()))
                .and(nameLike(searchCriteria.name()));
    }

    private Specification<PlayerEntity> tournamentIs(final UUID tournamentId) {
        if (tournamentId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("tournament").get("id"), tournamentId);
    }

    private Specification<PlayerEntity> nameLike(final String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name").as(String.class)),
                        "%" + name.toLowerCase() + "%");
    }
}
