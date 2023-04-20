package com.tretton37.ranking.elo.application.service.game;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.service.EloCalculatorService;
import com.tretton37.ranking.elo.domain.service.game.GameRegistrationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class GameRegistrationHandlerImpl implements GameRegistrationHandler {
    @Value("${elo.ranking.threshold-rank}")
    private Integer thresholdRank;
    private final EloCalculatorService eloCalculatorService;

    @Autowired
    public GameRegistrationHandlerImpl(EloCalculatorService eloCalculatorService) {
        this.eloCalculatorService = eloCalculatorService;
    }

    @Override
    public void init(Game game) {
        game.setId(UUID.randomUUID());
        game.setPlayedWhen(LocalDateTime.now());
        setWinner(game);
    }

    @Override
    public void captureRatingAlterations(Player playerA, Player playerB, Game game) {
        Map<Player, Integer> newRatings = eloCalculatorService.calculateRatings(playerA, playerB, game);
        newRatings.forEach((player, newRating) -> {
            trackResultRatingAlteration(player, newRating, game);
            updatePlayer(player, newRating, game);
        });
    }

    private void trackResultRatingAlteration(Player player, Integer newRating, Game game) {
        var playerResultA = game.getPlayerScoreA();
        var playerResultB = game.getPlayerScoreB();
        if (player.getId().equals(playerResultA.getPlayerRef().getId())) {
            playerResultA.setRatingAlteration(newRating - player.getRating());
        } else {
            playerResultB.setRatingAlteration(newRating - player.getRating());
        }
    }

    private void updatePlayer(Player player, int rating, Game game) {
        player.countGame(player.getId().equals(game.getWinnerId()));
        player.setRating(rating);
        if (rating > thresholdRank) {
            player.setReachedHighRating(Boolean.TRUE);
        }
    }

    private void setWinner(Game game) {
        var playerResultA = game.getPlayerScoreA();
        var playerResultB = game.getPlayerScoreB();
        if (game.getWinnerId() != null || playerResultA.getScore().equals(playerResultB.getScore())) {
            log.trace("setWinner: Winner calculation is not required for Game: {}", game);
            return;
        }
        if (playerResultA.getScore() > playerResultB.getScore()) {
            log.trace("setWinner: Winner is PlayerA");
            game.setWinnerId(playerResultA.getPlayerRef().getId());
        }
        if (playerResultB.getScore() > playerResultA.getScore()) {
            log.trace("setWinner: Winner is PlayerB");
            game.setWinnerId(playerResultB.getPlayerRef().getId());
        }
    }
}
