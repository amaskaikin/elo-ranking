package com.tretton37.ranking.elo.application.service.calculator;

import com.tretton37.ranking.elo.application.utils.EloCalculatorHelper;
import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.service.EloCalculatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class EloCalculatorServiceImpl implements EloCalculatorService {
    @Value("${elo.ranking.k-factor.max}")
    private Integer kFactorMax;
    @Value("${elo.ranking.k-factor.min}")
    private Integer kFactorMin;
    @Value("${elo.ranking.games-threshold}")
    private Integer gamesThreshold;

    private EloCalculatorHelper eloCalculatorHelper;

    @Override
    public Map<Player, Integer> calculateRatings(Player playerA, Player playerB, Game game) {
        log.debug("updateEloRatings: Calculating new rating for PlayerA: {} and PlayerB: {}. " +
                "game: {}", playerA, playerB, game);

        var actualScore = getCalculatorHelper().calculateActualScore(playerA, playerB,
                game.getWinnerId());
        double expectedScoreA = calculateExpectedScore(playerA.getRating(), playerB.getRating());
        double expectedScoreB = calculateExpectedScore(playerB.getRating(), playerA.getRating());
        log.debug("updateEloRatings: Actual score: {}, expected score for playerA: {}," +
                " expected score for playerB: {}", actualScore, expectedScoreA, expectedScoreB);

        int playerANewRating = calculateNewEloRating(playerA, expectedScoreA, actualScore.getPlayerAScore());
        int playerBNewRating = calculateNewEloRating(playerB, expectedScoreB, actualScore.getPlayerBScore());

        return Map.of(playerA, playerANewRating, playerB, playerBNewRating);
    }

    private int calculateKFactor(Player player) {
        log.trace("Calculating K-factor for Player: {}", player);
        if (player.getGamesPlayed() <= gamesThreshold) {
            log.trace("calculateKFactor: Player played games less than threshold, return K-factor: {}", kFactorMax);
            return kFactorMax;
        }
        if (!player.isReachedHighRating()) {
            int kFactor = kFactorMax / 2;
            log.trace("calculateKFactor: Player played more than threshold={} games, " +
                    "but never reached the high rank, return K-factor: {}", gamesThreshold, kFactor);
            return kFactor;
        }
        log.trace("calculateKFactor: Player played more than threshold={} games, and have reached the high rank, " +
                "return K-factor: {}", gamesThreshold, kFactorMin);
        return kFactorMin;
    }

    private double calculateExpectedScore(double eloRatingA, double eloRatingB) {
        return 1 / (1 + Math.pow(10, (eloRatingB - eloRatingA) / 400));
    }

    private int calculateNewEloRating(Player player, double expectedScore, double actualScore) {
        double currentEloRating = player.getRating();
        double kFactor = calculateKFactor(player);

        int calculatedRating = (int) Math.round(currentEloRating + kFactor * (actualScore - expectedScore));
        log.info("calculateNewEloRating: New Rating: {} for Player: {}", calculatedRating, player);

        return calculatedRating;
    }

    protected EloCalculatorHelper getCalculatorHelper() {
        return Objects.requireNonNullElseGet(this.eloCalculatorHelper, EloCalculatorHelper::new);
    }
}

