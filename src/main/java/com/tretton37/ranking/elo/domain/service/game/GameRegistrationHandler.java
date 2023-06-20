package com.tretton37.ranking.elo.domain.service.game;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;

public interface GameRegistrationHandler {
    Game init(Game game, Player playerA, Player playerB);
}
