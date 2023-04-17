package com.tretton37.ranking.elo.domain.service.game.validator;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.service.game.GameLifecycleStage;

public interface GameValidator {
    GameLifecycleStage getStage();
    void validate(Game game, Player initiator, Player opponent);
}
