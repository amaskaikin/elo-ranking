package com.tretton37.ranking.elo.application.service.achievement.auto;

import com.tretton37.ranking.elo.domain.model.Achievement;
import com.tretton37.ranking.elo.domain.model.AchievementDef;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.service.achievement.AchievementService;
import com.tretton37.ranking.elo.domain.service.achievement.AutoAchievement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;

@Component
@Slf4j
public class FirstLeetPlayerAchievementEvaluator implements AutoAchievement {
    private static final Integer LEET_THRESHOLD = 1337;

    private final AchievementService achievementService;

    @Autowired
    public FirstLeetPlayerAchievementEvaluator(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @Override
    public void apply(Player player) {
        if (player.getRating() >= LEET_THRESHOLD) {
            var firstLeetPlayer = loadAchievement();
            if (CollectionUtils.isEmpty(firstLeetPlayer.getPlayerIds())) {
                log.info("evaluate: Player {} is eligible for Achievement {}, assign",
                        player.getEmail(), firstLeetPlayer);

                addAchievement(firstLeetPlayer, player);
            } else {
                log.debug("evaluate: Achievement {} already granted to {}", firstLeetPlayer,
                        firstLeetPlayer.getPlayerIds());
            }
        }
    }

    private void addAchievement(Achievement achievement, Player player) {
        var achievements = player.getAchievements().orElse(new HashSet<>());
        achievements.add(achievement);
    }

    private Achievement loadAchievement() {
        return achievementService.getByType(getAchievementDef());
    }

    @Override
    public AchievementDef getAchievementDef() {
        return AchievementDef.FIRST_LEET_PLAYER;
    }
}
