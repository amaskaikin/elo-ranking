package com.tretton37.ranking.elo.application.service.game;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.exception.ErrorDetails;
import com.tretton37.ranking.elo.domain.model.exception.RequestConsistencyException;
import com.tretton37.ranking.elo.domain.service.game.GameValidator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GameInitValidator implements GameValidator {

    @Override
    public void validate(Game game, Player initiator, Player opponent) {
        validateWinnerAndScoresConsistency(game);
    }

    private void validateWinnerAndScoresConsistency(Game game) {
        var playerAScore = game.getPlayerScoreA().getScore();
        var playerBScore = game.getPlayerScoreB().getScore();
        UUID winnerId = game.getWinnerId();

        if (playerAScore != null && playerBScore != null) {
            if (playerAScore > playerBScore) {
                verifyWinnerId(winnerId, game.getPlayerScoreA().getPlayerRef().getId());
            }
            if (playerBScore > playerAScore) {
                verifyWinnerId(winnerId, game.getPlayerScoreB().getPlayerRef().getId());
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
