package com.tretton37.ranking.elo.application.service.game;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.PlayerRef;
import com.tretton37.ranking.elo.domain.model.PlayerScore;
import com.tretton37.ranking.elo.domain.service.PlayerService;
import com.tretton37.ranking.elo.domain.service.achievement.AutoAchievementManager;
import com.tretton37.ranking.elo.domain.service.game.GameRegistrationHandler;
import com.tretton37.ranking.elo.domain.service.game.GameValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameLifecycleManagerImplTest {
    @Mock
    private PlayerService playerService;
    @Mock
    private GameValidator gameInitValidator;
    @Mock
    private GameRegistrationHandler gameRegistrationHandler;
    @Mock
    private AutoAchievementManager autoAchievementManager;

    @InjectMocks
    private GameLifecycleManagerImpl gameLifecycleManager;

    @Test
    public void testRegister() {
        UUID playerAId = UUID.randomUUID();
        UUID playerBId = UUID.randomUUID();
        Player playerA = mock(Player.class);
        Player playerB = mock(Player.class);
        PlayerScore playerAScore = mock(PlayerScore.class);
        PlayerScore playerBScore = mock(PlayerScore.class);
        PlayerRef playerARef = mock(PlayerRef.class);
        PlayerRef playerBRef = mock(PlayerRef.class);
        Game game = mock(Game.class);

        when(game.getPlayerScoreA()).thenReturn(playerAScore);
        when(game.getPlayerScoreB()).thenReturn(playerBScore);
        when(playerAScore.getPlayerRef()).thenReturn(playerARef);
        when(playerBScore.getPlayerRef()).thenReturn(playerBRef);
        when(playerARef.getId()).thenReturn(playerAId);
        when(playerBRef.getId()).thenReturn(playerBId);
        when(playerService.findById(playerAId)).thenReturn(playerA);
        when(playerService.findById(playerBId)).thenReturn(playerB);
        doNothing().when(autoAchievementManager).evaluateAchievements(playerA, playerB);

        gameLifecycleManager.register(game);

        verify(playerService).findById(playerAId);
        verify(playerService).findById(playerBId);
        verify(gameInitValidator).validate(eq(game), eq(playerA), eq(playerB));
        verify(gameRegistrationHandler).init(eq(game));
        verify(gameRegistrationHandler).captureRatingAlterations(eq(playerA), eq(playerB), eq(game));
        verify(playerService).deltaUpdateBatch(List.of(playerA, playerB));
        verify(autoAchievementManager).evaluateAchievements(playerA, playerB);
    }
}
