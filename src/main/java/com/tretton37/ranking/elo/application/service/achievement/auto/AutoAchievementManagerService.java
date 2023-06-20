package com.tretton37.ranking.elo.application.service.achievement.auto;

import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.service.PlayerService;
import com.tretton37.ranking.elo.domain.service.achievement.AutoAchievement;
import com.tretton37.ranking.elo.domain.service.achievement.AutoAchievementManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class AutoAchievementManagerService implements AutoAchievementManager {
    private final PlayerService playerService;
    private final List<AutoAchievement> autoAchievements;

    @Autowired
    public AutoAchievementManagerService(PlayerService playerService,
                                         List<AutoAchievement> autoAchievements) {
        this.playerService = playerService;
        this.autoAchievements = autoAchievements;
    }

    @Override
    public void evaluateAchievements(Player... players) {
        var playersList = Stream.of(players).toList();
        playersList.forEach(p -> autoAchievements.forEach(evaluator -> evaluator.apply(p)));

        playerService.deltaUpdateBatch(playersList);
    }
}
