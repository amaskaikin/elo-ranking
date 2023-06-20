package com.tretton37.ranking.elo.application.service.game;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.service.PlayerService;
import com.tretton37.ranking.elo.domain.service.achievement.AutoAchievementManager;
import com.tretton37.ranking.elo.domain.service.game.GameLifecycleManager;
import com.tretton37.ranking.elo.domain.service.game.GameRegistrationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameLifecycleManagerImpl implements GameLifecycleManager {
    private final PlayerService playerService;
    private final GameRegistrationHandler gameRegistrationHandler;
    private final AutoAchievementManager autoAchievementManager;

    @Autowired
    public GameLifecycleManagerImpl(PlayerService playerService,
                                    GameRegistrationHandler gameRegistrationHandler,
                                    AutoAchievementManager autoAchievementManager) {
        this.playerService = playerService;
        this.gameRegistrationHandler = gameRegistrationHandler;
        this.autoAchievementManager = autoAchievementManager;
    }

    @Override
    public Game register(Game game) {
        var playerA = playerService.findById(game.getPlayerIdA());
        var playerB = playerService.findById(game.getPlayerIdB());

        Game created = gameRegistrationHandler.init(game, playerA, playerB);
        autoAchievementManager.evaluateAchievements(playerA, playerB);

        return created;
    }
}