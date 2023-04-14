package com.tretton37.ranking.elo.domain.service;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;

import java.util.Map;

public interface EloCalculatorService {
    Map<Player, Integer> calculateRatings(Player playerA, Player playerB, Game game);
}
