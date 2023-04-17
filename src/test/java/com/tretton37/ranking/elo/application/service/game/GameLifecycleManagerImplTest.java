package com.tretton37.ranking.elo.application.service.game;

import com.tretton37.ranking.elo.application.service.game.validator.GameValidatorFactory;
import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.GameStatus;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.PlayerRef;
import com.tretton37.ranking.elo.domain.service.PlayerService;
import com.tretton37.ranking.elo.domain.service.game.GameLifecycleStage;
import com.tretton37.ranking.elo.domain.service.game.GameRegistrationHandler;
import com.tretton37.ranking.elo.domain.service.game.validator.GameValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameLifecycleManagerImplTest {
    @Mock
    private PlayerService playerService;
    @Mock
    private GameValidatorFactory validatorFactory;
    @Mock
    private GameRegistrationHandler gameRegistrationHandler;
    @Mock
    private GameApprovalService gameApprovalService;
    @Mock
    private GameValidator gameValidator;

    @InjectMocks
    private GameLifecycleManagerImpl gameLifecycleManager;

    @BeforeEach
    public void setUp() {
        when(validatorFactory.getValidator(any(GameLifecycleStage.class))).thenReturn(gameValidator);
    }

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
        verify(validatorFactory).getValidator(eq(GameLifecycleStage.INIT));
        verify(gameValidator).validate(eq(game), eq(playerA), eq(playerB));
        verify(gameRegistrationHandler).init(eq(game));
        verify(gameRegistrationHandler).captureRatingAlterations(eq(playerA), eq(playerB), eq(game));
    }

    @Test
    public void testApprove() {
        UUID playerAId = UUID.randomUUID();
        UUID playerBId = UUID.randomUUID();
        Player initiator = mock(Player.class);
        Player opponent = mock(Player.class);
        PlayerRef playerARef = mock(PlayerRef.class);
        PlayerRef playerBRef = mock(PlayerRef.class);
        Game game = mock(Game.class);

        when(game.getPlayerRefA()).thenReturn(playerARef);
        when(game.getPlayerRefB()).thenReturn(playerBRef);
        when(playerARef.getId()).thenReturn(playerAId);
        when(playerBRef.getId()).thenReturn(playerBId);
        when(playerService.findById(playerAId)).thenReturn(initiator);
        when(playerService.findById(playerBId)).thenReturn(opponent);

        gameLifecycleManager.approve(game);

        verify(playerService).findById(playerAId);
        verify(playerService).findById(playerBId);
        verify(validatorFactory).getValidator(eq(GameLifecycleStage.APPROVE));
        verify(gameValidator).validate(eq(game), eq(initiator), eq(opponent));
        verify(gameApprovalService).approvePendingGame(eq(game), eq(initiator), eq(opponent));
    }

    @Test
    public void testDecline() {
        UUID playerAId = UUID.randomUUID();
        UUID playerBId = UUID.randomUUID();
        Player initiator = mock(Player.class);
        Player opponent = mock(Player.class);
        PlayerRef playerARef = mock(PlayerRef.class);
        PlayerRef playerBRef = mock(PlayerRef.class);
        Game game = mock(Game.class);

        when(game.getPlayerRefA()).thenReturn(playerARef);
        when(game.getPlayerRefB()).thenReturn(playerBRef);
        when(playerARef.getId()).thenReturn(playerAId);
        when(playerBRef.getId()).thenReturn(playerBId);
        when(playerService.findById(playerAId)).thenReturn(initiator);
        when(playerService.findById(playerBId)).thenReturn(opponent);

        gameLifecycleManager.decline(game);

        verify(playerService).findById(playerAId);
        verify(playerService).findById(playerBId);
        verify(validatorFactory).getValidator(eq(GameLifecycleStage.DECLINE));
        verify(gameValidator).validate(eq(game), eq(initiator), eq(opponent));
        verify(game).setStatus(eq(GameStatus.DECLINED));
    }
}
