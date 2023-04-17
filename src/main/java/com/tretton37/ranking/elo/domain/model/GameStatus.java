package com.tretton37.ranking.elo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GameStatus {
    PENDING,
    COMPLETED,
    DECLINED;
}