package com.tretton37.ranking.elo.application.service.game;

import com.tretton37.ranking.elo.adapter.persistence.GameGateway;
import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.service.EloCalculatorService;
import com.tretton37.ranking.elo.domain.service.PlayerService;
import com.tretton37.ranking.elo.domain.service.game.GameRegistrationHandler;
import com.tretton37.ranking.elo.domain.service.game.GameValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class GameRegistrationHandlerImpl implements GameRegistrationHandler {
    @Value("${elo.ranking.threshold-rank}")
    private Integer thresholdRank;
    private final GameValidator gameInitValidator;
    private final GameGateway gameGateway;
    private final PlayerService playerService;
    private final EloCalculatorService eloCalculatorService;

    @Autowired
    public GameRegistrationHandlerImpl(GameValidator gameInitValidator,
                                       GameGateway gameGateway,
                                       PlayerService playerService,
                                       EloCalculatorService eloCalculatorService) {
        this.gameInitValidator = gameInitValidator;
        this.gameGateway = gameGateway;
        this.playerService = playerService;
        this.eloCalculatorService = eloCalculatorService;
    }

    @Override
    public Game init(Game game, Player playerA, Player playerB) {
        gameInitValidator.validate(game);

        game.setId(UUID.randomUUID());
        game.setPlayedWhen(LocalDateTime.now());
        setWinner(game);
        updatePlayersRatings(game, playerA, playerB);

        return gameGateway.save(game);
    }

    // ToDo: Move to separate service as updating ratings may be required not only during initiation
    private void updatePlayersRatings(Game game, Player playerA, Player playerB) {
        var newRatings = eloCalculatorService.calculateRatings(playerA, playerB, game);
        newRatings.forEach((player, newRating) -> {
            trackRatingAlteration(player, newRating, game);
            updatePlayerStats(player, newRating, game);
        });

        playerService.deltaUpdateBatch(List.of(playerA, playerB));
    }

    private void trackRatingAlteration(Player player, Integer newRating, Game game) {
        var playerResultA = game.getPlayerScoreA();
        var playerResultB = game.getPlayerScoreB();
        if (player.getId().equals(playerResultA.getPlayerRef().getId())) {
            playerResultA.setRatingAlteration(newRating - player.getRating());
        } else {
            playerResultB.setRatingAlteration(newRating - player.getRating());
        }
    }

    private void updatePlayerStats(Player player, int rating, Game game) {
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
