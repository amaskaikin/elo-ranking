package com.tretton37.ranking.elo.domain.model.tournament.bracket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BracketSize {
    S64(64),
    S32(32),
    S16(16),
    S8(8),
    S4(4),
    S2(2);

    private final int value;

    public static BracketSize getByValue(int value) {
        for (BracketSize bracketSize : values()) {
            if (bracketSize.value == value) {
                return bracketSize;
            }
        }
        throw new IllegalArgumentException("Invalid BracketSize value: " + value);
    }
}

