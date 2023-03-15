package com.tretton37.ranking.elo.service.calculator;

import com.tretton37.ranking.elo.dto.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CalculatorHelperTest {
    @Spy
    private CalculatorHelper calculatorHelper;

    @Test
    public void testCalculateActualScore() {
        UUID playerIdA = UUID.randomUUID();
        UUID playerIdB = UUID.randomUUID();

        Player playerA = Player.builder().id(playerIdA).build();
        Player playerB = Player.builder().id(playerIdB).build();

        ActualScore resultAWin = calculatorHelper.calculateActualScore(playerA, playerB, playerIdA);
        assertEquals(1.0, resultAWin.getPlayerAScore());
        assertEquals(0.0, resultAWin.getPlayerBScore());

        ActualScore resultBWin = calculatorHelper.calculateActualScore(playerA, playerB, playerIdB);
        assertEquals(0.0, resultBWin.getPlayerAScore());
        assertEquals(1.0, resultBWin.getPlayerBScore());

        ActualScore draw = calculatorHelper.calculateActualScore(playerA, playerB, null);
        assertEquals(0.5, draw.getPlayerAScore());
        assertEquals(0.5, draw.getPlayerBScore());
    }
}
