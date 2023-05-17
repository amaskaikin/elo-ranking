package com.tretton37.ranking.elo.application.service.game;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.PlayerRef;
import com.tretton37.ranking.elo.domain.model.PlayerScore;
import com.tretton37.ranking.elo.domain.model.exception.RequestConsistencyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class GameInitValidatorTest {

    @Spy
    private GameInitValidator validator;

    @Test
    public void testValidate_validGame() {
        UUID playerAId = UUID.randomUUID();
        UUID playerBId = UUID.randomUUID();

        Game game = new Game();
        game.setPlayerScoreA(new PlayerScore(PlayerRef.builder().id(playerAId).build(), 10, null));
        game.setPlayerScoreB(new PlayerScore(PlayerRef.builder().id(playerBId).build(), 5, null));
        game.setWinnerId(playerAId);

        validator.validate(game, mock(Player.class), mock(Player.class));
    }

    @Test
    public void testValidate_notValidGame() {
        UUID playerAId = UUID.randomUUID();
        UUID playerBId = UUID.randomUUID();

        Game game = new Game();
        game.setPlayerScoreA(new PlayerScore(PlayerRef.builder().id(playerAId).build(), 10, null));
        game.setPlayerScoreB(new PlayerScore(PlayerRef.builder().id(playerBId).build(), 5, null));
        game.setWinnerId(playerBId);

        assertThrows(RequestConsistencyException.class, () ->
                validator.validate(game, mock(Player.class), mock(Player.class)));

        game.getPlayerScoreB().setScore(11);
        game.setWinnerId(playerAId);
        assertThrows(RequestConsistencyException.class, () ->
                validator.validate(game, mock(Player.class), mock(Player.class)));
    }
}
