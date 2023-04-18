package com.tretton37.ranking.elo.application.service.game;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.service.PlayerService;
import com.tretton37.ranking.elo.domain.service.achievement.AutoAchievementManager;
import com.tretton37.ranking.elo.domain.service.game.GameLifecycleManager;
import com.tretton37.ranking.elo.domain.service.game.GameRegistrationHandler;
import com.tretton37.ranking.elo.domain.service.game.GameValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameLifecycleManagerImpl implements GameLifecycleManager {

    private final PlayerService playerService;
    private final GameValidator gameInitValidator;
    private final GameRegistrationHandler gameRegistrationHandler;
    private final AutoAchievementManager autoAchievementManager;
    @Autowired
    public GameLifecycleManagerImpl(PlayerService playerService,
                                    GameValidator gameInitValidator,
                                    GameRegistrationHandler gameRegistrationHandler,
                                    AutoAchievementManager autoAchievementManager) {
        this.playerService = playerService;
        this.gameInitValidator = gameInitValidator;
        this.gameRegistrationHandler = gameRegistrationHandler;
        this.autoAchievementManager = autoAchievementManager;
    }

    @Override
    public void register(Game game) {
        Player playerA = playerService.findById(game.getPlayerScoreA().getPlayerRef().getId());
        Player playerB = playerService.findById(game.getPlayerScoreB().getPlayerRef().getId());

        gameInitValidator.validate(game, playerA, playerB);

        gameRegistrationHandler.init(game);
        gameRegistrationHandler.captureRatingAlterations(playerA, playerB, game);
        autoAchievementManager.evaluateAchievements(playerA, playerB);

        playerService.deltaUpdateBatch(List.of(playerA, playerB));
    }
}