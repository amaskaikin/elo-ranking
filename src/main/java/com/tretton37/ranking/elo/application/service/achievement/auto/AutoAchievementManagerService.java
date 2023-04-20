package com.tretton37.ranking.elo.application.service.achievement.auto;

import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.service.achievement.AutoAchievement;
import com.tretton37.ranking.elo.domain.service.achievement.AutoAchievementManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class AutoAchievementManagerService implements AutoAchievementManager {

    private final List<AutoAchievement> autoAchievements;

    @Autowired
    public AutoAchievementManagerService(List<AutoAchievement> autoAchievements) {
        this.autoAchievements = autoAchievements;
    }

    @Override
    public void evaluateAchievements(Player... players) {
        Arrays.stream(players).forEach(p ->
                autoAchievements.forEach(evaluator -> evaluator.apply(p))
        );
    }
}
