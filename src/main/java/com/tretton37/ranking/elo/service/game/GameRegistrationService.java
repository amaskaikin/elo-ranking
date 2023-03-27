package com.tretton37.ranking.elo.service.game;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.service.player.PlayerService;
import com.tretton37.ranking.elo.service.calculator.EloCalculatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GameRegistrationService {
    @Value("${elo.ranking.threshold-rank}")
    private Integer thresholdRank;

    private final PlayerService playerService;
    private final EloCalculatorService eloCalculatorService;

    @Autowired
    public GameRegistrationService(PlayerService playerService,
                                   EloCalculatorService eloCalculatorService) {
        this.playerService = playerService;
        this.eloCalculatorService = eloCalculatorService;
    }

    public Game registerGame(final Game game) {
        Player playerA = playerService.findById(game.getPlayerRefA().getId());
        Player playerB = playerService.findById(game.getPlayerRefB().getId());
        calculateWinner(game);

        Map<Player, Integer> newRatings = eloCalculatorService.calculateRatings(playerA, playerB, game);
        newRatings.forEach((player, newRating) -> {
            trackResultRatingAlteration(player, newRating, game);
            updatePlayer(player, newRating);
        });

        playerService.deltaUpdateBatch(List.of(playerA, playerB));

        game.setPlayedWhen(LocalDateTime.now());
        game.setPlayerRefA(playerService.convertDtoToReference(playerA));
        game.setPlayerRefB(playerService.convertDtoToReference(playerB));

        return game;
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

    private void calculateWinner(Game game) {
        Game.GameResult result = game.getGameResult();
        if (result.getWinnerId() != null || result.getPlayerAScore().equals(result.getPlayerBScore())) {
            log.trace("calculateWinner: Winner calculation is not required for Result: {}", result);
            return;
        }
        if (result.getPlayerAScore() > result.getPlayerBScore()) {
            log.trace("calculateWinner: Winner is PlayerA");
            result.setWinnerId(game.getPlayerRefA().getId());
        }
        if (result.getPlayerBScore() > result.getPlayerAScore()) {
            log.trace("calculateWinner: Winner is PlayerB");
            result.setWinnerId(game.getPlayerRefB().getId());
        }
    }
}
