package com.tretton37.ranking.elo.domain.service.achievement;

import com.tretton37.ranking.elo.domain.model.Player;

public interface AutoAchievementManager {
    void evaluateAchievements(Player... players);
}
