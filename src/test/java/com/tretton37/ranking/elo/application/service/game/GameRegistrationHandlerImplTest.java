package com.tretton37.ranking.elo.application.service.game;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.PlayerRef;
import com.tretton37.ranking.elo.domain.model.PlayerScore;
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
        PlayerScore playerScoreA = mock(PlayerScore.class);
        PlayerScore playerScoreB = mock(PlayerScore.class);
        PlayerRef playerRefA = mock(PlayerRef.class);

        doReturn(playerScoreA).when(game).getPlayerScoreA();
        doReturn(playerScoreB).when(game).getPlayerScoreB();
        doReturn(11).when(playerScoreA).getScore();
        doReturn(10).when(playerScoreB).getScore();
        doReturn(playerRefA).when(playerScoreA).getPlayerRef();
        doReturn(playerAId).when(playerRefA).getId();

        gameRegistrationHandler.init(game);

        verify(game).setPlayedWhen(any(LocalDateTime.class));
        verify(game).setWinnerId(playerAId);
    }

    @Test
    public void testCaptureRatingAlterations() {
        UUID playerIdA = UUID.randomUUID();
        UUID playerIdB = UUID.randomUUID();
        Player playerA = Player.builder().id(playerIdA).rating(1000).build();
        Player playerB = Player.builder().id(playerIdB).rating(1000).build();
        Game game = Game.builder()
                .playerScoreA(PlayerScore.builder()
                        .playerRef(PlayerRef.builder().id(playerIdA).build())
                        .score(2)
                        .build()
                )
                .playerScoreB(PlayerScore.builder()
                        .playerRef(PlayerRef.builder().id(playerIdB).build())
                        .score(1)
                        .build()
                )
                .build();
        when(eloCalculatorService.calculateRatings(playerA, playerB, game))
                .thenReturn(Map.of(playerA, 1020, playerB, 980));

        gameRegistrationHandler.captureRatingAlterations(playerA, playerB, game);

        assertEquals(20, game.getPlayerScoreA().getRatingAlteration());
        assertEquals(-20, game.getPlayerScoreB().getRatingAlteration());
        assertEquals(1020, playerA.getRating());
        assertEquals(980, playerB.getRating());
        verify(eloCalculatorService).calculateRatings(playerA, playerB, game);
    }
}
