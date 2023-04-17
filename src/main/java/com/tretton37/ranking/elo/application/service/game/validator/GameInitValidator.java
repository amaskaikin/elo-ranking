package com.tretton37.ranking.elo.application.service.game.validator;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.exception.ErrorDetails;
import com.tretton37.ranking.elo.domain.model.exception.RequestConsistencyException;
import com.tretton37.ranking.elo.domain.service.game.GameLifecycleStage;
import com.tretton37.ranking.elo.domain.service.game.validator.GameValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Component
public class GameInitValidator implements GameValidator {
    @Override
    public GameLifecycleStage getStage() {
        return GameLifecycleStage.INIT;
    }

    @Override
    public void validate(Game game, Player initiator, Player opponent) {
        validateWinnerAndScoresConsistency(game);
        validateNoPendingGamesAgainstOthers(initiator, opponent);
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

    private void validateNoPendingGamesAgainstOthers(Player initiator, Player opponent) {
        if (CollectionUtils.isEmpty(initiator.getPendingGameRefs())
                && CollectionUtils.isEmpty(opponent.getPendingGameRefs())) {
            return;
        }

        verifyInitiatorAndApproverConsistency(initiator, opponent);
        verifyInitiatorAndApproverConsistency(opponent, initiator);

    }

    private void verifyInitiatorAndApproverConsistency(Player playerA, Player playerB) {
        Optional.ofNullable(playerA.getPendingGameRefs()).orElse(Collections.emptyList())
                .forEach(pendingGame -> {
                    if (playerA.getId().equals(pendingGame.getInitiator().getId())) {
                        if (!playerB.getId().equals(pendingGame.getApprover().getId())) {
                            throw new RequestConsistencyException(ErrorDetails.BAD_REQUEST, "There are pending " +
                                    "games against other opponents found for player: " + playerA.getName());
                        }
                    }
                    if (playerA.getId().equals(pendingGame.getApprover().getId())) {
                        if (!playerB.getId().equals(pendingGame.getInitiator().getId())) {
                            throw new RequestConsistencyException(ErrorDetails.BAD_REQUEST, "There are pending " +
                                    "games against other opponents found for player: " + playerA.getName());
                        }
                    }
                });
    }

    private void verifyWinnerId(UUID requestWinnerId, UUID winnerIdByScore) {
        if (requestWinnerId != null && !requestWinnerId.equals(winnerIdByScore)) {
            throw new RequestConsistencyException(ErrorDetails.BAD_REQUEST,
                    "Winner is not consistent with the high score");
        }
    }
}
