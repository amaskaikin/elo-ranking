package com.tretton37.ranking.elo.application.service.calculator;

import com.tretton37.ranking.elo.domain.model.PlayerScore;
import com.tretton37.ranking.elo.domain.model.calculator.ActualScore;
import com.tretton37.ranking.elo.application.utils.EloCalculatorHelper;
import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.PlayerRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
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
public class EloCalculatorServiceImplTest {
    @Mock
    private EloCalculatorHelper eloCalculatorHelper;

    @Spy
    private EloCalculatorServiceImpl eloCalculatorService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(eloCalculatorService, "kFactorMax", 40);
        ReflectionTestUtils.setField(eloCalculatorService, "kFactorMin", 10);
        ReflectionTestUtils.setField(eloCalculatorService, "gamesThreshold", 30);

        doReturn(eloCalculatorHelper).when(eloCalculatorService).getCalculatorHelper();
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
                .playerScoreA(PlayerScore.builder()
                                .playerRef(PlayerRef.builder().id(playerIdA).build())
                                .build()
                )
                .playerScoreB(PlayerScore.builder()
                        .playerRef(PlayerRef.builder().id(playerIdB).build())
                        .build()
                )
                .winnerId(playerIdA)
                .build();

        ActualScore actualScore = mock(ActualScore.class);
        doReturn(actualScore).when(eloCalculatorHelper)
                .calculateActualScore(eq(playerA), eq(playerB), eq(playerIdA));
        when(actualScore.getPlayerAScore()).thenReturn(1.0);
        when(actualScore.getPlayerBScore()).thenReturn(0.0);

        Map<Player, Integer> newRatings = eloCalculatorService.calculateRatings(playerA, playerB, game);

        assertThat(newRatings, aMapWithSize(2));
        assertEquals(1614, newRatings.get(playerA));
        assertEquals(1486, newRatings.get(playerB));

        verify(eloCalculatorHelper).calculateActualScore(playerA, playerB, playerIdA);
    }
}
