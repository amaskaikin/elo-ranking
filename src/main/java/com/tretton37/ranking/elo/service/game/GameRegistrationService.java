package com.tretton37.ranking.elo.service.game;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.service.player.PlayerService;
import com.tretton37.ranking.elo.service.calculator.EloCalculatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class GameRegistrationService {
    private final PlayerService playerService;
    private final EloCalculatorService eloCalculatorService;

    @Autowired
    public GameRegistrationService(PlayerService playerService,
                                   EloCalculatorService eloCalculatorService) {
        this.playerService = playerService;
        this.eloCalculatorService = eloCalculatorService;
    }

    public Game registerGame(Game game) {
        Player playerA = playerService.findById(game.getPlayerRefA().getId());
        Player playerB = playerService.findById(game.getPlayerRefB().getId());
        calculateWinner(game);

        eloCalculatorService.updateEloRatings(playerA, playerB, game);

        playerService.deltaUpdateBatch(List.of(playerA, playerB));
        game.setPlayedWhen(LocalDateTime.now());
        game.setPlayerRefA(playerService.convertDtoToReference(playerA));
        game.setPlayerRefB(playerService.convertDtoToReference(playerB));

        return game;
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
