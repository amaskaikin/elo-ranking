package com.tretton37.ranking.elo.adapter.persistence;

import com.tretton37.ranking.elo.adapter.mappers.PlayerMapper;
import com.tretton37.ranking.elo.application.persistence.entity.PlayerEntity;
import com.tretton37.ranking.elo.application.persistence.repository.PlayerRepository;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.search.PlayerListFilteringCriteria;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerGatewayTest {
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PlayerMapper playerMapper;
    @Mock
    private Page<PlayerEntity> pageEntityMock;

    @InjectMocks
    private PlayerGateway playerGateway;

    @SuppressWarnings("unchecked")
    @Test
    public void testFind() {
        PlayerListFilteringCriteria filteringCriteria =
                new PlayerListFilteringCriteria(UUID.randomUUID(), 1);
        doReturn(pageEntityMock).when(playerRepository)
                .findAll(any(Specification.class), any(Pageable.class));
        doReturn(new PageImpl<>(Collections.singletonList(mock(Player.class))))
                .when(pageEntityMock).map(any());

        Page<Player> result = playerGateway.list(filteringCriteria, Pageable.unpaged());

        assertThat(result.getContent(), hasSize(1));
        verify(playerRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testFindById() {
        UUID id = UUID.randomUUID();
        PlayerEntity playerEntity = mock(PlayerEntity.class);
        Player player = mock(Player.class);

        when(playerRepository.findById(id)).thenReturn(Optional.of(playerEntity));
        when(playerMapper.entityToDto(playerEntity)).thenReturn(player);

        Optional<Player> resultOptional = playerGateway.findById(id);
        assertTrue(resultOptional.isPresent());

        assertThat(resultOptional.get(), equalTo(player));
        verify(playerRepository).findById(id);
        verify(playerMapper).entityToDto(playerEntity);
    }

    @Test
    public void testSave() {
        Player player = Player.builder().build();
        PlayerEntity playerEntity = mock(PlayerEntity.class);
        when(playerMapper.dtoToEntity(player)).thenReturn(playerEntity);
        when(playerRepository.save(playerEntity)).thenReturn(playerEntity);
        when(playerMapper.entityToDto(playerEntity)).thenReturn(player);

        Player createdPlayer = playerGateway.save(player);

        verify(playerRepository).save(playerEntity);
    }

    @Test
    public void testSaveAll() {
        List<Player> players = Collections.singletonList(mock(Player.class));
        when(playerMapper.dtoToEntity(any(Player.class))).thenReturn(mock(PlayerEntity.class));

        playerGateway.saveAll(players);

        verify(playerRepository).saveAll(anyCollection());
    }
}
