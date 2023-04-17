package com.tretton37.ranking.elo.application.service.game.validator;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.exception.ErrorDetails;
import com.tretton37.ranking.elo.domain.model.exception.RequestConsistencyException;
import com.tretton37.ranking.elo.domain.service.game.GameLifecycleStage;
import com.tretton37.ranking.elo.domain.service.game.validator.PendingGameValidator;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Stream;

@Component
public class GameApproveValidator implements PendingGameValidator {

    @Override
    public GameLifecycleStage getStage() {
        return GameLifecycleStage.APPROVE;
    }

    @Override
    public void validate(Game game, Player initiator, Player approver) {
        validateGameIsPending(game);
        validatePlayerIsEligibleToApprove(approver);
        validateGameIsTheEarliest(game, approver);
    }

    // ToDo: will be always true, leaving as reminder to implement validation via Access Token
    private void validatePlayerIsEligibleToApprove(Player approver) {
        if (Stream.ofNullable(approver.getPendingGameRefs())
                .flatMap(Collection::stream)
                .noneMatch(pg -> approver.getId().equals(pg.getApprover().getId()))) {
            throw new RequestConsistencyException(ErrorDetails.BAD_REQUEST,
                    "Player '" + approver.getName() + "' is not eligible to approve the requested game");
        };

    }

    private void validateGameIsTheEarliest(Game game, Player approver) {
        if (Stream.ofNullable(approver.getPendingGameRefs()).flatMap(Collection::stream)
                .anyMatch(pending -> pending.getPlayedWhen().isBefore(game.getPlayedWhen()))) {
            throw new RequestConsistencyException(ErrorDetails.BAD_REQUEST,
                    "There are earlier pending games found");
        }
    }
}

