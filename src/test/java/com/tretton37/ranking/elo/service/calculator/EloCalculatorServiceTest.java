package com.tretton37.ranking.elo.service.calculator;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.dto.PlayerRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EloCalculatorServiceTest {
    @Mock
    private CalculatorHelper calculatorHelper;

    @InjectMocks
    private EloCalculatorService eloCalculatorService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(eloCalculatorService, "kFactorMax", 40);
        ReflectionTestUtils.setField(eloCalculatorService, "kFactorMin", 10);
        ReflectionTestUtils.setField(eloCalculatorService, "gamesThreshold", 30);
    }

    @Test
    public void testUpdateEloRatings() {
        UUID playerIdA = UUID.randomUUID();
        UUID playerIdB = UUID.randomUUID();
        Player playerA = Player.builder()
                .id(playerIdA)
                .rating(1600)
                .gamesPlayed(10)
                .build();

        Player playerB = Player.builder()
                .id(playerIdB)
                .rating(1500)
                .gamesPlayed(10)
                .reachedHighRating(false)
                .build();

        Game game = Game.builder()
                .playerRefA(PlayerRef.builder().id(playerIdA).build())
                .playerRefB(PlayerRef.builder().id(playerIdB).build())
                .gameResult(Game.GameResult.builder()
                        .winnerId(playerIdA)
                        .build())
                .build();

        ActualScore actualScore = mock(ActualScore.class);
        doReturn(actualScore).when(calculatorHelper)
                .calculateActualScore(eq(playerA), eq(playerB), eq(playerIdA));
        when(actualScore.getPlayerAScore()).thenReturn(1.0);
        when(actualScore.getPlayerBScore()).thenReturn(0.0);

        Map<Player, Integer> newRatings = eloCalculatorService.calculateRatings(playerA, playerB, game);

        assertThat(newRatings, aMapWithSize(2));
        assertEquals(1614, newRatings.get(playerA));
        assertEquals(1486, newRatings.get(playerB));

        verify(calculatorHelper).calculateActualScore(playerA, playerB, playerIdA);
    }
}
