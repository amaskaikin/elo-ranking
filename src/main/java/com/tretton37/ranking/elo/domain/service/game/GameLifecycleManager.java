package com.tretton37.ranking.elo.domain.service.game;

import com.tretton37.ranking.elo.domain.model.Game;

public interface GameLifecycleManager {
    Game register(Game game);
}
