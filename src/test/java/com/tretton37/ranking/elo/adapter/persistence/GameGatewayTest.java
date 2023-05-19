package com.tretton37.ranking.elo.adapter.persistence;

import com.tretton37.ranking.elo.adapter.mappers.GameMapper;
import com.tretton37.ranking.elo.application.persistence.entity.GameEntity;
import com.tretton37.ranking.elo.application.persistence.repository.GameRepository;
import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.search.GameSearchCriteria;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameGatewayTest {
    @Mock
    private GameRepository gameRepository;
    @Mock
    private GameMapper mapper;
    @Mock
    private Page<GameEntity> pageEntityMock;

    @InjectMocks
    private GameGateway gameGateway;

    @Test
    public void testGetAll() {
        doReturn(new PageImpl<>(Collections.singletonList(mock(Game.class))))
                .when(pageEntityMock).map(any());
        when(gameRepository.findAll(any(Pageable.class))).thenReturn(pageEntityMock);

        Page<Game> result = gameGateway.getAll(Pageable.unpaged());

        assertThat(result.getContent(), hasSize(1));
        verify(gameRepository).findAll(any(Pageable.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFind() {
        GameSearchCriteria searchCriteria = new GameSearchCriteria(null, null, UUID.randomUUID());
        Page<Game> gameMock = mock(Page.class);
        Pageable pageable = Pageable.unpaged();
        doReturn(pageEntityMock).when(gameRepository).findAll(any(Specification.class), any(Pageable.class));
        doReturn(gameMock).when(pageEntityMock).map(any());

        Page<Game> result = gameGateway.find(searchCriteria, pageable);

        verify(gameRepository).findAll(any(Specification.class), eq(pageable));
        assertThat(result, equalTo(gameMock));
    }

    @Test
    public void testFindById_shouldReturnGame() {
        UUID id = UUID.randomUUID();
        GameEntity gameEntity = mock(GameEntity.class);
        Game game = mock(Game.class);

        when(gameRepository.findById(id)).thenReturn(Optional.of(gameEntity));
        when(mapper.entityToDto(gameEntity)).thenReturn(game);

        Optional<Game> resultOptional = gameGateway.findById(id);

        assertTrue(resultOptional.isPresent());
        assertThat(resultOptional.get(), equalTo(game));
        verify(gameRepository).findById(id);
        verify(mapper).entityToDto(gameEntity);
    }

    @Test
    public void testDelete_shouldDeleteGameById() {
        UUID id = UUID.randomUUID();
        gameGateway.delete(id);
        verify(gameRepository).deleteById(id);
    }
}
