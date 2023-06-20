package com.tretton37.ranking.elo.domain.service.tournament;

import com.tretton37.ranking.elo.domain.model.Game;

public interface TournamentHandler {
    void evaluate(Game game);
}
