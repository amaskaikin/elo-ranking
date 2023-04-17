package com.tretton37.ranking.elo.application.service.game;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.GameStatus;
import com.tretton37.ranking.elo.domain.model.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GameApprovalService {
    @Value("${elo.ranking.threshold-rank}")
    private Integer thresholdRank;

    public void approvePendingGame(Game game, Player approver, Player opponent) {
        var gameResult = game.getGameResult();
        updatePlayerParamsOnApprove(approver, gameResult.getPlayerARatingAlteration());
        updatePlayerParamsOnApprove(opponent, gameResult.getPlayerBRatingAlteration());

        game.setStatus(GameStatus.COMPLETED);
    }

    private void updatePlayerParamsOnApprove(Player player, int ratingAlteration) {
        int newRating = player.getRating() + ratingAlteration;
        log.debug("updatePlayer: setting new rating: {}, for player: {}", newRating, player);

        player.setRating(player.getRating() + ratingAlteration);
        if (player.getRating() > this.thresholdRank) {
            player.setReachedHighRating(Boolean.TRUE);
        }
        player.countGame();
    }
}
