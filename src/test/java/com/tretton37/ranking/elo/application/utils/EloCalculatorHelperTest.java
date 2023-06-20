package com.tretton37.ranking.elo.application.utils;

import com.tretton37.ranking.elo.domain.model.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EloCalculatorHelperTest {
    @Spy
    private EloCalculatorHelper eloCalculatorHelper;

    @Test
    public void testCalculateActualScore() {
        var playerIdA = UUID.randomUUID();
        var playerIdB = UUID.randomUUID();

        var playerA = Player.builder().id(playerIdA).build();
        var playerB = Player.builder().id(playerIdB).build();

        var resultAWin = eloCalculatorHelper.calculateActualScore(playerA, playerB, playerIdA);
        assertEquals(1.0, resultAWin.getPlayerAScore());
        assertEquals(0.0, resultAWin.getPlayerBScore());

        var resultBWin = eloCalculatorHelper.calculateActualScore(playerA, playerB, playerIdB);
        assertEquals(0.0, resultBWin.getPlayerAScore());
        assertEquals(1.0, resultBWin.getPlayerBScore());

        var draw = eloCalculatorHelper.calculateActualScore(playerA, playerB, null);
        assertEquals(0.5, draw.getPlayerAScore());
        assertEquals(0.5, draw.getPlayerBScore());
    }
}
