package com.tretton37.ranking.elo.application.persistence.specification;

import com.tretton37.ranking.elo.application.persistence.entity.PlayerEntity;
import com.tretton37.ranking.elo.domain.model.search.PlayerFilteringCriteria;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

import static org.springframework.data.jpa.domain.Specification.where;

public class PlayerSpecificationBuilder {

    private final PlayerFilteringCriteria filteringCriteria;

    public static PlayerSpecificationBuilder forCriteria(PlayerFilteringCriteria playerFilteringCriteria) {
        return new PlayerSpecificationBuilder(playerFilteringCriteria);
    }

    private PlayerSpecificationBuilder(PlayerFilteringCriteria filteringCriteria) {
        this.filteringCriteria = filteringCriteria;
    }

    public Specification<PlayerEntity> build() {
        return where(locationIs(filteringCriteria.locationId()))
                .and(minGamesPlayed(filteringCriteria.gamesPlayed()))
                .and(emailIs(filteringCriteria.email()))
                .and(nameNormalizedContaining(filteringCriteria.name()));
    }

    private Specification<PlayerEntity> locationIs(final UUID locationId) {
        if (locationId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("location").get("id"), locationId);
    }

    private Specification<PlayerEntity> minGamesPlayed(final Integer value) {
        if (value == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.ge(root.get("gamesPlayed").as(Integer.class), value);
    }

    private Specification<PlayerEntity> emailIs(final String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("email"), value);
    }

    private Specification<PlayerEntity> nameNormalizedContaining(final String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return (root, query, builder) ->
                builder.like(
                        builder.lower(
                                builder.function("unaccent", String.class, root.get("name"))
                        ), builder.lower(
                                builder.function("unaccent", String.class, builder.literal("%" + value + "%"))
                        )
                );
    }
}
