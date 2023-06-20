package com.tretton37.ranking.elo.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AchievementDef {
    MANUAL("Manual achievement assigned by administrator"),
    FIRST_LEET_PLAYER("The first player who reached leet(1337) rating");

    private final String description;
}
