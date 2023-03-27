package com.tretton37.ranking.elo.service.game;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.dto.PlayerRef;
import com.tretton37.ranking.elo.service.calculator.EloCalculatorService;
import com.tretton37.ranking.elo.service.player.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
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

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(gameRegistrationService, "thresholdRank", 2400);
    }

    @Test
    public void testRegisterGame() {
        UUID playerIdA = UUID.randomUUID();
        UUID playerIdB = UUID.randomUUID();
        Player playerA = Player.builder().id(playerIdA).rating(1000).build();
        Player playerB = Player.builder().id(playerIdB).rating(1000).build();
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
        when(eloCalculatorService.calculateRatings(playerA, playerB, game))
                .thenReturn(Map.of(playerA, 1020, playerB, 980));

        Game registeredGame = gameRegistrationService.registerGame(game);

        assertNotNull(registeredGame.getPlayedWhen());
        assertEquals(playerIdA, game.getGameResult().getWinnerId());
        assertEquals(20, game.getGameResult().getPlayerARatingAlteration());
        assertEquals(-20, game.getGameResult().getPlayerBRatingAlteration());
        assertEquals(1020, playerA.getRating());
        assertEquals(980, playerB.getRating());
        verify(playerService).deltaUpdateBatch(List.of(playerA, playerB));
        verify(eloCalculatorService).calculateRatings(playerA, playerB, game);
    }
}
