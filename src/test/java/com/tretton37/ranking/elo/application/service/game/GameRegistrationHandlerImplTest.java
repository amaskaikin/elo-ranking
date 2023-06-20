package com.tretton37.ranking.elo.application.service.game;

import com.tretton37.ranking.elo.adapter.persistence.GameGateway;
import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.PlayerRef;
import com.tretton37.ranking.elo.domain.model.PlayerScore;
import com.tretton37.ranking.elo.domain.service.EloCalculatorService;
import com.tretton37.ranking.elo.domain.service.PlayerService;
import com.tretton37.ranking.elo.domain.service.game.GameValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameRegistrationHandlerImplTest {
    @Mock
    private GameValidator gameInitValidator;
    @Mock
    private GameGateway gameGateway;
    @Mock
    private PlayerService playerService;
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
        var game = mock(Game.class);
        var playerAId = UUID.randomUUID();
        var playerScoreA = mock(PlayerScore.class);
        var playerScoreB = mock(PlayerScore.class);
        var playerRefA = mock(PlayerRef.class);

        doReturn(playerScoreA).when(game).getPlayerScoreA();
        doReturn(playerScoreB).when(game).getPlayerScoreB();
        doReturn(11).when(playerScoreA).getScore();
        doReturn(10).when(playerScoreB).getScore();
        doReturn(playerRefA).when(playerScoreA).getPlayerRef();
        doReturn(playerAId).when(playerRefA).getId();

        doNothing().when(gameInitValidator).validate(any(Game.class));
        doReturn(game).when(gameGateway).save(game);

        gameRegistrationHandler.init(game);

        verify(game).setPlayedWhen(any(LocalDateTime.class));
        verify(game).setWinnerId(playerAId);
        verify(gameGateway).save(game);
        verify(gameInitValidator).validate(game);
    }

    @Test
    public void testCaptureRatingAlterations() {
        var playerIdA = UUID.randomUUID();
        var playerIdB = UUID.randomUUID();
        var playerA = Player.builder().id(playerIdA).rating(1000).build();
        var playerB = Player.builder().id(playerIdB).rating(1000).build();

        var game = new Game();
        game.setPlayerScoreA(new PlayerScore(PlayerRef.builder().id(playerIdA).build(), 2, null));
        game.setPlayerScoreB(new PlayerScore(PlayerRef.builder().id(playerIdB).build(), 1, null));
        game.setWinnerId(playerIdA);

        doNothing().when(playerService).deltaUpdateBatch(anyCollection());
        when(eloCalculatorService.calculateRatings(playerA, playerB, game))
                .thenReturn(Map.of(playerA, 1020, playerB, 980));

        gameRegistrationHandler.updatePlayersRatings(game, playerA, playerB);

        assertEquals(20, game.getPlayerScoreA().getRatingAlteration());
        assertEquals(-20, game.getPlayerScoreB().getRatingAlteration());
        assertEquals(1020, playerA.getRating());
        assertEquals(980, playerB.getRating());
        assertEquals(1, playerA.getGamesWon());
        assertEquals(0, playerB.getGamesWon());
        assertEquals(1, playerA.getGamesPlayed());
        assertEquals(1, playerB.getGamesPlayed());

        verify(eloCalculatorService).calculateRatings(playerA, playerB, game);
        verify(eloCalculatorService).calculateRatings(playerA, playerB, game);
        verify(playerService).deltaUpdateBatch(List.of(playerA, playerB));
    }
}
