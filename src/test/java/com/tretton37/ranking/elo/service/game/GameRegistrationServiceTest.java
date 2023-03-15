package com.tretton37.ranking.elo.service.game;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.dto.PlayerRef;
import com.tretton37.ranking.elo.service.calculator.EloCalculatorService;
import com.tretton37.ranking.elo.service.player.PlayerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameRegistrationServiceTest {
    @Mock
    private PlayerService playerService;
    @Mock
    private EloCalculatorService eloCalculatorService;

    @InjectMocks
    private GameRegistrationService gameRegistrationService;

    @Test
    public void testRegisterGame() {
        UUID playerIdA = UUID.randomUUID();
        UUID playerIdB = UUID.randomUUID();
        Player playerA = Player.builder().id(playerIdA).build();
        Player playerB = Player.builder().id(playerIdB).build();
        Game game = Game.builder()
                .playerRefA(PlayerRef.builder().id(playerIdA).build())
                .playerRefB(PlayerRef.builder().id(playerIdB).build())
                .gameResult(Game.GameResult.builder()
                        .playerAScore(2)
                        .playerBScore(1)
                        .build())
                .build();

        when(playerService.findById(playerIdA)).thenReturn(playerA);
        when(playerService.findById(playerIdB)).thenReturn(playerB);

        Game registeredGame = gameRegistrationService.registerGame(game);

        assertNotNull(registeredGame.getPlayedWhen());
        assertEquals(playerIdA, game.getGameResult().getWinnerId());
        verify(playerService).deltaUpdateBatch(List.of(playerA, playerB));
        verify(eloCalculatorService).updateEloRatings(playerA, playerB, game);
    }
}
