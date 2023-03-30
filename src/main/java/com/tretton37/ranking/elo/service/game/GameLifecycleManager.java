package com.tretton37.ranking.elo.service.game;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.service.player.PlayerService;
import com.tretton37.ranking.elo.service.validator.GameValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameLifecycleManager {

    private final PlayerService playerService;
    private final GameValidator gameInitValidator;
    private final GameRegistrarHelper gameRegistrarHelper;
    @Autowired
    public GameLifecycleManager(PlayerService playerService,
                                GameValidator gameInitValidator,
                                GameRegistrarHelper gameRegistrarHelper) {
        this.playerService = playerService;
        this.gameInitValidator = gameInitValidator;
        this.gameRegistrarHelper = gameRegistrarHelper;
    }

    public void register(Game game) {
        Player playerA = playerService.findById(game.getPlayerRefA().getId());
        Player playerB = playerService.findById(game.getPlayerRefB().getId());

        gameInitValidator.validate(game, playerA, playerB);

        gameRegistrarHelper.init(game);
        gameRegistrarHelper.captureRatingAlterations(playerA, playerB, game);

        playerService.deltaUpdateBatch(List.of(playerA, playerB));
    }
}