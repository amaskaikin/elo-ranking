package com.tretton37.ranking.elo.service.calculator;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EloCalculatorService {
    @Value("${elo.ranking.threshold-rank}")
    private Integer thresholdRank;
    @Value("${elo.ranking.k-factor.max}")
    private Integer kFactorMax;
    @Value("${elo.ranking.k-factor.min}")
    private Integer kFactorMin;
    @Value("${elo.ranking.games-threshold}")
    private Integer gamesThreshold;

    private final CalculatorHelper calculatorHelper;

    @Autowired
    public EloCalculatorService(CalculatorHelper calculatorHelper) {
        this.calculatorHelper = calculatorHelper;
    }

    public void updateEloRatings(Player playerA, Player playerB, Game game) {
        log.debug("updateEloRatings: Calculating new rating for PlayerA: {} and PlayerB: {}. " +
                "game: {}", playerA, playerB, game);

        ActualScore actualScore = calculatorHelper.calculateActualScore(playerA, playerB,
                game.getGameResult().getWinnerId());
        double expectedScoreA = calculateExpectedScore(playerA.getRating(), playerB.getRating());
        double expectedScoreB = calculateExpectedScore(playerB.getRating(), playerA.getRating());
        log.debug("updateEloRatings: Actual score: {}, expected score for playerA: {}," +
                " expected score for playerB: {}", actualScore, expectedScoreA, expectedScoreB);

        int playerANewRating = calculateNewEloRating(playerA, expectedScoreA, actualScore.getPlayerAScore());
        int playerBNewRating = calculateNewEloRating(playerB, expectedScoreB, actualScore.getPlayerBScore());

        updateRating(playerA, playerANewRating);
        updateRating(playerB, playerBNewRating);
    }

    private void updateRating(Player player, int rating) {
        player.setRating(rating);
        if (rating > thresholdRank) {
            player.setReachedHighRating(Boolean.TRUE);
        }
    }

    private int calculateKFactor(Player player) {
        log.trace("Calculating K-factor for Player: {}", player);
        Integer gamesPlayed = player.getGamesPlayed();
        if (gamesPlayed > gamesThreshold) {
            if (!player.isReachedHighRating()) {
                int kFactor = kFactorMax / 2;
                log.trace("calculateKFactor: Player played more than threshold={} games, but never reached the high rank, " +
                        "return K-factor: {}", gamesThreshold, kFactor);
                return kFactor;
            }
            log.trace("calculateKFactor: Player played more than threshold={} games, and have reached the high rank, " +
                    "return K-factor: {}", gamesThreshold, kFactorMin);
            return kFactorMin;
        }

        log.trace("calculateKFactor: Player played games less than threshold, return K-factor: {}", kFactorMax);
        return kFactorMax;
    }

    private double calculateExpectedScore(double eloRatingA, double eloRatingB) {
        return 1 / (1 + Math.pow(10, (eloRatingB - eloRatingA) / 400));
    }

    private int calculateNewEloRating(Player player, double expectedScore, double actualScore) {
        double currentEloRating = player.getRating();
        double kFactor = calculateKFactor(player);

        int calculatedRating = (int) (currentEloRating + kFactor * (actualScore - expectedScore));
        log.info("calculateNewEloRating: New Rating: {} for Player: {}", calculatedRating, player);

        return calculatedRating;
    }
}

