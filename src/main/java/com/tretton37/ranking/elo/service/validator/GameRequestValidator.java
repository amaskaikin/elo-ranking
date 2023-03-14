package com.tretton37.ranking.elo.service.validator;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.errorhandling.ErrorDetails;
import com.tretton37.ranking.elo.errorhandling.RequestConsistencyException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GameRequestValidator implements RequestValidator<Game> {

    @Override
    public void validate(Game game) {
        validateResult(game);
    }

    private void validateResult(Game game) {
        Game.GameResult result = game.getGameResult();
        UUID winnerId = result.getWinnerId();

        if (result.getPlayerAScore() != null && result.getPlayerBScore() != null) {
            if (result.getPlayerAScore() > result.getPlayerBScore()) {
                if (winnerId != null && !winnerId.equals(game.getPlayerRefA().getId())) {
                    throw new RequestConsistencyException(ErrorDetails.BAD_REQUEST,
                            "Winner is not consistent with the high score");
                }
            }
            if (result.getPlayerBScore() > result.getPlayerAScore()) {
                if (winnerId != null && !winnerId.equals(game.getPlayerRefB().getId())) {
                    throw new RequestConsistencyException(ErrorDetails.BAD_REQUEST,
                            "Winner is not consistent with the high score");
                }
            }
        }
    }
}
