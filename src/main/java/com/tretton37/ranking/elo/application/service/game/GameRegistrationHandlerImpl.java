package com.tretton37.ranking.elo.application.service.game;

import com.tretton37.ranking.elo.adapter.persistence.GameGateway;
import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.GameStatus;
import com.tretton37.ranking.elo.domain.model.PendingGameRef;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.service.EloCalculatorService;
import com.tretton37.ranking.elo.domain.service.game.GameRegistrationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Slf4j
public class GameRegistrationHandlerImpl implements GameRegistrationHandler {
    @Value("${elo.ranking.threshold-rank}")
    private Integer thresholdRank;
    private final EloCalculatorService eloCalculatorService;
    private final GameGateway gameGateway;

    @Autowired
    public GameRegistrationHandlerImpl(EloCalculatorService eloCalculatorService,
                                       GameGateway gameGateway) {
        this.eloCalculatorService = eloCalculatorService;
        this.gameGateway = gameGateway;
    }

    public void captureRatingAlterations(Player playerA, Player playerB, Game game) {
        countPendingGamesRatingAlteration(playerA, playerB);
        Map<Player, Integer> newRatings = eloCalculatorService.calculateRatings(playerA, playerB, game);
        newRatings.forEach((player, newRating) ->
                trackResultRatingAlteration(player, newRating, game)
        );
    }

    public void init(Game game) {
        game.setId(UUID.randomUUID());
        game.setStatus(GameStatus.PENDING);
        game.setPlayedWhen(LocalDateTime.now());
        setWinner(game);
    }

    private void countPendingGamesRatingAlteration(Player playerA, Player playerB) {
        Integer pendingAlterationA = Stream.ofNullable(playerA.getPendingGameRefs())
                .flatMap(Collection::stream)
                .map(pendingGame -> getPendingGameResult(pendingGame)
                        .getPlayerARatingAlteration())
                .mapToInt(Integer::intValue)
                .sum();
        Integer pendingAlterationB = Stream.ofNullable(playerB.getPendingGameRefs())
                .flatMap(Collection::stream)
                .map(pendingGame -> getPendingGameResult(pendingGame)
                        .getPlayerBRatingAlteration())
                .mapToInt(Integer::intValue)
                .sum();

        log.trace("countPendingGamesRatingAlteration: pendingAlterationA={}, pendingAlterationB={}",
                pendingAlterationA, pendingAlterationB);
        playerA.setPendingRating(playerA.getRating() + pendingAlterationA);
        playerB.setPendingRating(playerB.getRating() + pendingAlterationB);
    }

    // ToDo: Refactor - not ok to call repository here
    private Game.GameResult getPendingGameResult(PendingGameRef gameRef) {
        return gameGateway.findById(gameRef.getGameId())
                .map(Game::getGameResult)
                .orElse(null);
    }

    private void trackResultRatingAlteration(Player player, Integer newRating, Game game) {
        var gameResult = game.getGameResult();
        if (player.getId().equals(game.getPlayerRefA().getId())) {
            gameResult.setPlayerARatingAlteration(newRating - player.getRatingIncludePending());
        } else {
            gameResult.setPlayerBRatingAlteration(newRating - player.getRatingIncludePending());
        }
    }

    private void setWinner(Game game) {
        Game.GameResult result = game.getGameResult();
        if (result.getWinnerId() != null || result.getPlayerAScore().equals(result.getPlayerBScore())) {
            log.trace("setWinner: Winner calculation is not required for Result: {}", result);
            return;
        }
        if (result.getPlayerAScore() > result.getPlayerBScore()) {
            log.trace("setWinner: Winner is PlayerA");
            result.setWinnerId(game.getPlayerRefA().getId());
        }
        if (result.getPlayerBScore() > result.getPlayerAScore()) {
            log.trace("setWinner: Winner is PlayerB");
            result.setWinnerId(game.getPlayerRefB().getId());
        }
    }
}
