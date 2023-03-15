package com.tretton37.ranking.elo.service.calculator;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ActualScore {
    private double playerAScore;
    private double playerBScore;

    public static ActualScoreCalculator forResult(GameResult result) {
        return new ActualScoreCalculator(result);
    }

    public static class ActualScoreCalculator {
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
                    this.actualScore.playerBScore = 0.5;
                }
                default -> throw new RuntimeException("Unknown game result: " + result);
            }

            return this.actualScore;
        }
    }
}
