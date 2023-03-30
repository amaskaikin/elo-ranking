package com.tretton37.ranking.elo.service.validator;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.Player;

public interface GameValidator {
    void validate(Game game, Player initiator, Player opponent);
}
