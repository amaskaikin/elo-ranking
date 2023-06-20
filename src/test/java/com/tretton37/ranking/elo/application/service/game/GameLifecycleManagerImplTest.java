package com.tretton37.ranking.elo.application.service.game;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.service.PlayerService;
import com.tretton37.ranking.elo.domain.service.achievement.AutoAchievementManager;
import com.tretton37.ranking.elo.domain.service.game.GameRegistrationHandler;
import com.tretton37.ranking.elo.domain.service.tournament.TournamentHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameLifecycleManagerImplTest {
    @Mock
    private PlayerService playerService;
    @Mock
    private GameRegistrationHandler gameRegistrationHandler;
    @Mock
    private AutoAchievementManager autoAchievementManager;
    @Mock
    private TournamentHandler tournamentHandler;

    @InjectMocks
    private GameLifecycleManagerImpl gameLifecycleManager;

    @Test
    public void testRegister() {
        var playerAId = UUID.randomUUID();
        var playerBId = UUID.randomUUID();
        var playerA = mock(Player.class);
        var playerB = mock(Player.class);
        var game = mock(Game.class);

        when(game.getPlayerIdA()).thenReturn(playerAId);
        when(game.getPlayerIdB()).thenReturn(playerBId);
        when(playerService.findById(playerAId)).thenReturn(playerA);
        when(playerService.findById(playerBId)).thenReturn(playerB);
        doNothing().when(autoAchievementManager).evaluateAchievements(playerA, playerB);
        doReturn(game).when(gameRegistrationHandler).init(game, playerA, playerB);

        var registered = gameLifecycleManager.register(game);

        verify(playerService).findById(playerAId);
        verify(playerService).findById(playerBId);
        verify(gameRegistrationHandler).init(eq(game), eq(playerA), eq(playerB));
        verify(autoAchievementManager).evaluateAchievements(playerA, playerB);
        verify(tournamentHandler).evaluate(game);
    }
}
