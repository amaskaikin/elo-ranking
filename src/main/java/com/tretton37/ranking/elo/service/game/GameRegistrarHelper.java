package com.tretton37.ranking.elo.service.game;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.service.calculator.EloCalculatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class GameRegistrarHelper {
    @Value("${elo.ranking.threshold-rank}")
    private Integer thresholdRank;
    private final EloCalculatorService eloCalculatorService;

    @Autowired
    public GameRegistrarHelper(EloCalculatorService eloCalculatorService) {
        this.eloCalculatorService = eloCalculatorService;
    }

    public void captureRatingAlterations(Player playerA, Player playerB, Game game) {
        Map<Player, Integer> newRatings = eloCalculatorService.calculateRatings(playerA, playerB, game);
        newRatings.forEach((player, newRating) -> {
            trackResultRatingAlteration(player, newRating, game);
            updatePlayer(player, newRating);
        });
    }

    public void init(Game game) {
        game.setId(UUID.randomUUID());
        game.setPlayedWhen(LocalDateTime.now());
        setWinner(game);
    }

    private void trackResultRatingAlteration(Player player, Integer newRating, Game game) {
        var gameResult = game.getGameResult();
        if (player.getId().equals(game.getPlayerRefA().getId())) {
            gameResult.setPlayerARatingAlteration(newRating - player.getRating());
        } else {
            gameResult.setPlayerBRatingAlteration(newRating - player.getRating());
        }
    }

    private void updatePlayer(Player player, int rating) {
        player.countGame();
        player.setRating(rating);
        if (rating > thresholdRank) {
            player.setReachedHighRating(Boolean.TRUE);
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
