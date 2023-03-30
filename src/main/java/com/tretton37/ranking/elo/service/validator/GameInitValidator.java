package com.tretton37.ranking.elo.service.validator;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.errorhandling.ErrorDetails;
import com.tretton37.ranking.elo.errorhandling.RequestConsistencyException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GameInitValidator implements GameValidator {

    @Override
    public void validate(Game game, Player initiator, Player opponent) {
        validateWinnerAndScoresConsistency(game);
    }

    private void validateWinnerAndScoresConsistency(Game game) {
        var result = game.getGameResult();
        var playerAScore = result.getPlayerAScore();
        var playerBScore = result.getPlayerBScore();
        UUID winnerId = result.getWinnerId();

        if (playerAScore != null && playerBScore != null) {
            if (playerAScore > playerBScore) {
                verifyWinnerId(winnerId, game.getPlayerRefA().getId());
            }
            if (playerBScore > playerAScore) {
                verifyWinnerId(winnerId, game.getPlayerRefB().getId());
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
