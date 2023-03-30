package com.tretton37.ranking.elo.service.game;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.dto.PlayerRef;
import com.tretton37.ranking.elo.service.player.PlayerService;
import com.tretton37.ranking.elo.service.validator.GameValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameLifecycleManagerTest {
    @Mock
    private PlayerService playerService;
    @Mock
    private GameValidator gameInitValidator;
    @Mock
    private GameRegistrarHelper gameRegistrarHelper;

    @InjectMocks
    private GameLifecycleManager gameLifecycleManager;

    @Test
    public void testRegister() {
        UUID playerAId = UUID.randomUUID();
        UUID playerBId = UUID.randomUUID();
        Player playerA = mock(Player.class);
        Player playerB = mock(Player.class);
        PlayerRef playerARef = mock(PlayerRef.class);
        PlayerRef playerBRef = mock(PlayerRef.class);
        Game game = mock(Game.class);

        when(game.getPlayerRefA()).thenReturn(playerARef);
        when(game.getPlayerRefB()).thenReturn(playerBRef);
        when(playerARef.getId()).thenReturn(playerAId);
        when(playerBRef.getId()).thenReturn(playerBId);
        when(playerService.findById(playerAId)).thenReturn(playerA);
        when(playerService.findById(playerBId)).thenReturn(playerB);

        gameLifecycleManager.register(game);

        verify(playerService).findById(playerAId);
        verify(playerService).findById(playerBId);
        verify(gameInitValidator).validate(eq(game), eq(playerA), eq(playerB));
        verify(gameRegistrarHelper).init(eq(game));
        verify(gameRegistrarHelper).captureRatingAlterations(eq(playerA), eq(playerB), eq(game));
        verify(playerService).deltaUpdateBatch(List.of(playerA, playerB));
    }
}
