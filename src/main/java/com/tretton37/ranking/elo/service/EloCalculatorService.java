package com.tretton37.ranking.elo.service;

import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.persistence.entity.GameResult;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EloCalculatorService {
    @Value("${elo.ranking.scaling}")
    private Integer scaling;
    @Value("${elo.ranking.initial-rank}")
    private Integer referenceRating;

    @Value("${elo.ranking.k-factor.max}")
    private Integer kFactorMax;
    @Value("${elo.ranking.k-factor.min}")
    private Integer kFactorMin;
    @Value("${elo.ranking.games-threshold.min}")
    private Integer gamesThresholdMin;
    @Value("${elo.ranking.games-threshold.min}")
    private Integer gamesThresholdMax;

    public void updateEloRatings(Player playerA, Player playerB, GameResult gameResult) {
        log.debug("updateEloRatings: Calculating new rating for PlayerA: {} and PlayerB: {}. " +
                "GameResult: {}", playerA, playerB, gameResult);

        ActualScore actualScore = ActualScore.forResult(gameResult).calculate();
        double expectedScoreA = calculateExpectedScore(playerA.getRating(), playerB.getRating());
        double expectedScoreB = calculateExpectedScore(playerB.getRating(), playerA.getRating());
        log.debug("updateEloRatings: Actual score: {}, expected score for playerA: {}," +
                " expected score for playerB: {}", actualScore, expectedScoreA, expectedScoreB);

        playerA.setRating(calculateNewEloRating(playerA, expectedScoreA, actualScore.getPlayerAScore()));
        playerB.setRating(calculateNewEloRating(playerB, expectedScoreB, actualScore.getPlayerBScore()));
    }

    private int calculateKFactor(Player player) {
        log.debug("Calculating K-factor for Player: {}", player);
        Integer gamesPlayed = player.getGamesPlayed();
        if (gamesPlayed >= gamesThresholdMax) {
            log.debug("calculateKFactor: Games played more or equal to max threshold, " +
                    "return min K-factor {}", kFactorMin);
            return kFactorMin;
        }
        if (gamesPlayed >= ((gamesThresholdMin + gamesThresholdMax) / 2)) {
            int kFactor = (kFactorMin + kFactorMax) / 2;
            log.debug("calculateKFactor: Games played more or equal to average between Max and Min thresholds, " +
                    "return average K-factor {}", kFactor);

            return kFactor;
        }
        if (gamesPlayed >= gamesThresholdMin) {
            int kFactor = kFactorMax - kFactorMin;
            log.debug("calculateKFactor: Games played more or equal to min threshold, " +
                    "return diff subtraction between max and min K-factor: {}", kFactor);

            return kFactor;
        }
        int kFactor = calculateKFactorForNewPlayer((double) player.getRating());
        log.debug("calculateKFactor: Player played games less than min threshold, calculated K-factor: {}", kFactor);

        return kFactor;
    }

    private Integer calculateKFactorForNewPlayer(Double rating) {
        int kFactor = (int) Math.round(kFactorMax - (rating - referenceRating) / scaling);

        return Math.max(kFactor, 10);
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

    @ToString
    @Getter
    private static class ActualScore {
        private double playerAScore;
        private double playerBScore;

        public static ActualScoreCalculator forResult(GameResult result) {
            return new ActualScoreCalculator(result);
        }

        private static class ActualScoreCalculator {
            private final GameResult result;

            private final ActualScore actualScore;

            private ActualScoreCalculator(GameResult result) {
                this.result = result;
                this.actualScore = new ActualScore();
            }

            public ActualScore calculate() {
                switch (result) {
                    case PLAYERA_WIN -> {
                        this.actualScore.playerAScore = 1.0;
                        this.actualScore.playerBScore = 0.0;
                    }
                    case PLAYERB_WIN -> {
                        this.actualScore.playerAScore = 0.0;
                        this.actualScore.playerBScore = 1.0;
                    }
                    case DRAW -> {
                        this.actualScore.playerAScore = 0.5;
                        this.actualScore.playerBScore = 1.5;
                    }
                    default -> throw new RuntimeException("Unknown game result: " + result);
                }

                return this.actualScore;
            }
        }
    }
}

