package com.tretton37.ranking.elo.service.game;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.mapper.PersistenceMapper;
import com.tretton37.ranking.elo.dto.search.GameSearchCriteria;
import com.tretton37.ranking.elo.errorhandling.EntityNotFoundException;
import com.tretton37.ranking.elo.errorhandling.ErrorDetails;
import com.tretton37.ranking.elo.persistence.GameRepository;
import com.tretton37.ranking.elo.persistence.entity.GameEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {
    @Mock
    private GameRepository gameRepository;
    @Mock
    private PersistenceMapper<Game, GameEntity> mapper;
    @Mock
    private GameRegistrationService registrationService;
    @Mock
    private Page<GameEntity> pageEntityMock;

    @InjectMocks
    private GameService gameService;

    @Test
    public void testGetGames() {
        doReturn(new PageImpl<>(Collections.singletonList(mock(Game.class))))
                .when(pageEntityMock).map(any());
        when(gameRepository.findAll(any(Pageable.class))).thenReturn(pageEntityMock);

        Page<Game> result = gameService.getGames(Pageable.unpaged());

        assertThat(result.getContent(), hasSize(1));
        verify(gameRepository).findAll(any(Pageable.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFindGames() {
        GameSearchCriteria searchCriteria = new GameSearchCriteria(null, null, UUID.randomUUID());
        Page<Game> gameMock = mock(Page.class);
        Pageable pageable = Pageable.unpaged();
        doReturn(pageEntityMock).when(gameRepository).findAll(any(Specification.class), any(Pageable.class));
        doReturn(gameMock).when(pageEntityMock).map(any());

        Page<Game> result = gameService.findGames(searchCriteria, pageable);

        verify(gameRepository).findAll(any(Specification.class), eq(pageable));
        assertThat(result, equalTo(gameMock));
    }

    @Test
    public void testFindGameById_shouldReturnGame() {
        UUID id = UUID.randomUUID();
        GameEntity gameEntity = mock(GameEntity.class);
        Game game = mock(Game.class);

        when(gameRepository.findById(id)).thenReturn(Optional.of(gameEntity));
        when(mapper.entityToDto(gameEntity)).thenReturn(game);

        Game result = gameService.findGameById(id);

        assertThat(result, equalTo(game));
        verify(gameRepository).findById(id);
        verify(mapper).entityToDto(gameEntity);
    }

    @Test
    public void testFindGameById_shouldThrowExceptionIfGameNotFound() {
        UUID id = UUID.randomUUID();

        when(gameRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> gameService.findGameById(id));

        verify(gameRepository).findById(id);
        assertEquals(ErrorDetails.ENTITY_NOT_FOUND, exception.getErrorDetails());
        assertEquals(ErrorDetails.ENTITY_NOT_FOUND, exception.getErrorDetails());
    }

    @Test
    public void testRegisterGame_shouldRegisterGame() {
        Game gameToRegisterMock = mock(Game.class);
        Game createdMock = mock(Game.class);
        GameEntity createdEntityMock = new GameEntity();

        when(registrationService.registerGame(gameToRegisterMock)).thenReturn(createdMock);
        when(mapper.dtoToEntity(createdMock)).thenReturn(createdEntityMock);
        when(gameRepository.save(createdEntityMock)).thenReturn(createdEntityMock);
        when(mapper.entityToDto(createdEntityMock)).thenReturn(createdMock);

        Game result = gameService.registerGame(gameToRegisterMock);

        assertEquals(createdMock, result);
        verify(registrationService).registerGame(gameToRegisterMock);
        verify(mapper).dtoToEntity(createdMock);
        verify(gameRepository).save(createdEntityMock);
        verify(mapper).entityToDto(createdEntityMock);
    }

    @Test
    public void testDeleteGame_shouldDeleteGameById() {
        UUID id = UUID.randomUUID();
        gameService.deleteGame(id);
        verify(gameRepository).deleteById(id);
    }
}
