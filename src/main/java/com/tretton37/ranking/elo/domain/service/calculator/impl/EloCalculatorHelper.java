package com.tretton37.ranking.elo.domain.service.calculator.impl;

import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.calculator.ActualScore;
import com.tretton37.ranking.elo.domain.model.calculator.GameResult;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class EloCalculatorHelper {
    public ActualScore calculateActualScore(Player playerA, Player playerB, UUID winnerId) {
        var gameResult = resolveGameResult(playerA.getId(), playerB.getId(), winnerId);
        log.debug("calculateActualScore: Resolved GameResult: {}", gameResult);

        return ActualScore.forResult(gameResult).calculate();
    }

    private GameResult resolveGameResult(UUID playerA, UUID playerB, UUID winner) {
        if (winner == null) {
            return GameResult.DRAW;
        }
        if (winner.equals(playerA)) {
            return GameResult.PLAYERA_WIN;
        }
        if (winner.equals(playerB)) {
            return GameResult.PLAYERB_WIN;
        }

        throw new RuntimeException(String.format("Couldn't determine Game result: playerA=%s, " +
                "playerB=%s, winner=%s", playerA, playerB, winner));
    }
}
