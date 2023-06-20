package com.tretton37.ranking.elo.application.service.game;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.exception.ErrorDetails;
import com.tretton37.ranking.elo.domain.model.exception.RequestConsistencyException;
import com.tretton37.ranking.elo.domain.service.game.GameValidator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GameInitValidator implements GameValidator {

    @Override
    public void validate(Game game) {
        validateWinnerAndScoresConsistency(game);
    }

    private void validateWinnerAndScoresConsistency(Game game) {
        var playerAScore = game.getPlayerScoreA().getScore();
        var playerBScore = game.getPlayerScoreB().getScore();
        var winnerId = game.getWinnerId();

        if (playerAScore != null && playerBScore != null) {
            if (playerAScore > playerBScore) {
                verifyWinnerId(winnerId, game.getPlayerIdA());
            }
            if (playerBScore > playerAScore) {
                verifyWinnerId(winnerId, game.getPlayerIdB());
            }
        }
    }

    private void verifyWinnerId(UUID requestWinnerId, UUID winnerIdByScore) {
        if (requestWinnerId != null && !requestWinnerId.equals(winnerIdByScore)) {
            throw new RequestConsistencyException(ErrorDetails.BAD_REQUEST,
                    "Winner is not consistent with the high score");
        }
    }
}
