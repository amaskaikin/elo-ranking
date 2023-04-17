package com.tretton37.ranking.elo.domain.service.game.validator;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.GameStatus;
import com.tretton37.ranking.elo.domain.model.exception.ErrorDetails;
import com.tretton37.ranking.elo.domain.model.exception.RequestConsistencyException;

public interface PendingGameValidator extends GameValidator {
    default void validateGameIsPending(Game game) {
        if (!GameStatus.PENDING.equals(game.getStatus())) {
            throw new RequestConsistencyException(ErrorDetails.BAD_REQUEST,
                    "Game is invalid status. Expected: '" + GameStatus.PENDING
                            + "'. Actual: '" + game.getStatus() + "'");
        }
    }
}