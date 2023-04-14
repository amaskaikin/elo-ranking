package com.tretton37.ranking.elo.domain.service.game;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;

public interface GameValidator {
    void validate(Game game, Player initiator, Player opponent);
}
