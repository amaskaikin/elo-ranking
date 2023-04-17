package com.tretton37.ranking.elo.application.service.game;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.PlayerRef;
import com.tretton37.ranking.elo.domain.service.EloCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameRegistrationHandlerImplTest {

    @Mock
    private EloCalculatorService eloCalculatorService;

    @InjectMocks
    private GameRegistrationHandlerImpl gameRegistrationHandler;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(gameRegistrationHandler, "thresholdRank", 2400);
    }

    @Test
    public void testInit() {
        Game game = mock(Game.class);
        UUID playerAId = UUID.randomUUID();
        PlayerRef playerRefA = mock(PlayerRef.class);
        Game.GameResult gameResult = mock(Game.GameResult.class);

        doReturn(gameResult).when(game).getGameResult();
        doReturn(11).when(gameResult).getPlayerAScore();
        doReturn(10).when(gameResult).getPlayerBScore();
        doReturn(playerRefA).when(game).getPlayerRefA();
        doReturn(playerAId).when(playerRefA).getId();

        gameRegistrationHandler.init(game);

        verify(game).setPlayedWhen(any(LocalDateTime.class));
        verify(gameResult).setWinnerId(playerAId);

    }

    @Test
    public void testCaptureRatingAlterations() {
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
        when(eloCalculatorService.calculateRatings(playerA, playerB, game))
                .thenReturn(Map.of(playerA, 1020, playerB, 980));

        gameRegistrationHandler.captureRatingAlterations(playerA, playerB, game);

        assertEquals(20, game.getGameResult().getPlayerARatingAlteration());
        assertEquals(-20, game.getGameResult().getPlayerBRatingAlteration());
        assertEquals(1000, playerA.getRating());
        assertEquals(1000, playerB.getRating());
        verify(eloCalculatorService).calculateRatings(playerA, playerB, game);
    }
}
