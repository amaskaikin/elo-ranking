package com.tretton37.ranking.elo.domain.service.achievement;

import com.tretton37.ranking.elo.domain.model.AchievementDef;
import com.tretton37.ranking.elo.domain.model.Player;

public interface AutoAchievement {
    void apply(Player player);

    AchievementDef getAchievementDef();
}
