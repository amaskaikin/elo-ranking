package com.tretton37.ranking.elo.service.validator;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.PlayerRef;
import com.tretton37.ranking.elo.errorhandling.RequestConsistencyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class GameRequestValidatorTest {

    @Spy
    private GameRequestValidator validator;

    @Test
    public void testValidate_validGame() {
        UUID playerAId = UUID.randomUUID();
        UUID playerBId = UUID.randomUUID();

        Game game = Game.builder()
                .playerRefA(PlayerRef.builder().id(playerAId).build())
                .playerRefB(PlayerRef.builder().id(playerBId).build())
                .gameResult(Game.GameResult.builder()
                        .playerAScore(10)
                        .playerBScore(5)
                        .winnerId(playerAId)
                        .build()
                )
                .build();
        validator.validate(game);
    }

    @Test
    public void testValidate_notValidGame() {
        UUID playerAId = UUID.randomUUID();
        UUID playerBId = UUID.randomUUID();

        Game game = Game.builder()
                .playerRefA(PlayerRef.builder().id(playerAId).build())
                .playerRefB(PlayerRef.builder().id(playerBId).build())
                .gameResult(Game.GameResult.builder()
                        .playerAScore(10)
                        .playerBScore(5)
                        .winnerId(playerBId)
                        .build()
                )
                .build();
        assertThrows(RequestConsistencyException.class, () -> validator.validate(game));

        game.getGameResult().setPlayerBScore(11);
        game.getGameResult().setWinnerId(playerAId);
        assertThrows(RequestConsistencyException.class, () -> validator.validate(game));
    }
}
