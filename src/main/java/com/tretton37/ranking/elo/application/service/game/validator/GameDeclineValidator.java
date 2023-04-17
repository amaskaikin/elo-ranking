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
public class GameDeclineValidator implements PendingGameValidator {

    @Override
    public GameLifecycleStage getStage() {
        return GameLifecycleStage.DECLINE;
    }

    @Override
    public void validate(Game game, Player playerA, Player playerB) {
        validateGameIsPending(game);
        verifyGameIsTheLatest(game, playerA);
        verifyGameIsTheLatest(game, playerB);
    }

    private void verifyGameIsTheLatest(Game game, Player player) {
        if (Stream.ofNullable(player.getPendingGameRefs()).flatMap(Collection::stream)
                .anyMatch(pending -> pending.getPlayedWhen().isAfter(game.getPlayedWhen()))) {
            throw new RequestConsistencyException(ErrorDetails.BAD_REQUEST,
                    "There are latest pending games found for player: " + player.getName());
        }
    }
}
