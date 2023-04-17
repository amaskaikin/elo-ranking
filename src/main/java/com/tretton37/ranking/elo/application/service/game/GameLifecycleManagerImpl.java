package com.tretton37.ranking.elo.application.service.game;

import com.tretton37.ranking.elo.application.service.game.validator.GameValidatorFactory;
import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.GameStatus;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.service.PlayerService;
import com.tretton37.ranking.elo.domain.service.game.GameLifecycleManager;
import com.tretton37.ranking.elo.domain.service.game.GameLifecycleStage;
import com.tretton37.ranking.elo.domain.service.game.GameRegistrationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameLifecycleManagerImpl implements GameLifecycleManager {

    private final PlayerService playerService;
    private final GameValidatorFactory validatorFactory;
    private final GameRegistrationHandler gameRegistrationHandler;
    private final GameApprovalService gameApprovalService;
    @Autowired
    public GameLifecycleManagerImpl(PlayerService playerService,
                                    GameValidatorFactory validatorFactory,
                                    GameRegistrationHandler gameRegistrationHandler,
                                    GameApprovalService gameApprovalService) {
        this.playerService = playerService;
        this.validatorFactory = validatorFactory;
        this.gameRegistrationHandler = gameRegistrationHandler;
        this.gameApprovalService = gameApprovalService;
    }

    @Override
    public void register(Game game) {
        Player playerA = playerService.findById(game.getPlayerRefA().getId());
        Player playerB = playerService.findById(game.getPlayerRefB().getId());

        validatorFactory.getValidator(GameLifecycleStage.INIT).validate(game, playerA, playerB);

        gameRegistrationHandler.init(game);
        gameRegistrationHandler.captureRatingAlterations(playerA, playerB, game);

        playerService.deltaUpdateBatch(List.of(playerA, playerB));
    }

    @Override
    public void approve(Game game) {
        // Assuming that approver always playerB
        // ToDo: Fetch info from access token instead
        Player initiator = playerService.findById(game.getPlayerRefA().getId());
        Player approver = playerService.findById(game.getPlayerRefB().getId());

        validatorFactory.getValidator(GameLifecycleStage.APPROVE).validate(game, initiator, approver);

        gameApprovalService.approvePendingGame(game, initiator, approver);

        playerService.deltaUpdateBatch(List.of(initiator, approver));
    }

    @Override
    public void decline(Game game) {
        Player playerA = playerService.findById(game.getPlayerRefA().getId());
        Player playerB = playerService.findById(game.getPlayerRefB().getId());

        validatorFactory.getValidator(GameLifecycleStage.DECLINE).validate(game, playerA, playerB);

        game.setStatus(GameStatus.DECLINED);
    }
}