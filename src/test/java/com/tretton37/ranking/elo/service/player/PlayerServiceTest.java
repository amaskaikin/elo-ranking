package com.tretton37.ranking.elo.service.player;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.dto.mapper.PlayerMapper;
import com.tretton37.ranking.elo.dto.search.PlayerListFilteringCriteria;
import com.tretton37.ranking.elo.persistence.PlayerRepository;
import com.tretton37.ranking.elo.persistence.entity.PlayerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PlayerMapper playerMapper;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private Page<PlayerEntity> pageEntityMock;

    @InjectMocks
    private PlayerService playerService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(playerService, "initialRank", 1000);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetPlayers() {
        PlayerListFilteringCriteria filteringCriteria =
                new PlayerListFilteringCriteria(UUID.randomUUID(), 1);
        doReturn(pageEntityMock).when(playerRepository)
                .findAll(any(Specification.class), any(Pageable.class));
        doReturn(new PageImpl<>(Collections.singletonList(mock(Player.class))))
                .when(pageEntityMock).map(any());

        Page<Player> result = playerService.getPlayers(filteringCriteria, Pageable.unpaged());

        assertThat(result.getContent(), hasSize(1));
        verify(playerRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testFind() {
        String searchName = "player";
        doReturn(Collections.singletonList(mock(PlayerEntity.class)))
                .when(playerRepository).findAllByNameContainingIgnoreCase(eq(searchName));

        Collection<Player> result = playerService.find(searchName);

        verify(playerRepository).findAllByNameContainingIgnoreCase(searchName);
        assertThat(result, hasSize(1));
    }

    @Test
    public void testFindById() {
        UUID id = UUID.randomUUID();
        PlayerEntity playerEntity = mock(PlayerEntity.class);
        Player player = mock(Player.class);

        when(playerRepository.findById(id)).thenReturn(Optional.of(playerEntity));
        when(playerMapper.entityToDto(playerEntity)).thenReturn(player);

        Player result = playerService.findById(id);

        assertThat(result, equalTo(player));
        verify(playerRepository).findById(id);
        verify(playerMapper).entityToDto(playerEntity);
    }

    @Test
    public void testCreate() {
        Player player = Player.builder().build();
        PlayerEntity playerEntity = mock(PlayerEntity.class);
        when(playerRepository.findByEmail(player.getEmail())).thenReturn(null);
        when(playerMapper.dtoToEntity(player)).thenReturn(playerEntity);
        when(playerRepository.save(playerEntity)).thenReturn(playerEntity);
        when(playerMapper.entityToDto(playerEntity)).thenReturn(player);

        Player createdPlayer = playerService.create(player);

        assertThat(createdPlayer, notNullValue());
        assertThat(createdPlayer.getRegisteredWhen(), notNullValue());
        assertEquals(1000, createdPlayer.getRating());
        verify(playerRepository).save(playerEntity);
    }

    @Test
    public void testBulkCreate() {
        List<Player> players = Collections.singletonList(mock(Player.class));
        when(playerMapper.dtoToEntity(any(Player.class))).thenReturn(mock(PlayerEntity.class));

        playerService.bulkCreate(players);

        verify(playerRepository).saveAll(anyCollection());
    }

    @Test
    public void testDeltaUpdate() throws IOException {
        ObjectReader objectReaderMock = mock(ObjectReader.class);

        PlayerEntity existingEntityMock = mock(PlayerEntity.class);
        PlayerEntity updatedEntityMock = mock(PlayerEntity.class);
        Player existingMock = mock(Player.class);
        Player updatedMock = mock(Player.class);
        Player mergedMock = mock(Player.class);

        doReturn(Optional.of(existingEntityMock)).when(playerRepository).findById(any(UUID.class));
        doReturn(existingMock).when(playerMapper).entityToDto(existingEntityMock);
        doReturn(updatedEntityMock).when(playerMapper).dtoToEntity(any(Player.class));
        doReturn(updatedEntityMock).when(playerRepository).save(updatedEntityMock);
        doReturn(mergedMock).when(playerMapper).entityToDto(updatedEntityMock);

        doReturn(objectReaderMock).when(objectMapper).readerForUpdating(existingMock);
        doReturn(mock(JsonNode.class)).when(objectMapper).convertValue(any(Player.class), eq(JsonNode.class));
        doReturn(updatedMock).when(objectReaderMock).readValue(any(JsonNode.class));

        Player updated = playerService.deltaUpdate(UUID.randomUUID(), Player.builder().build());

        assertEquals(mergedMock, updated);
        verify(playerRepository).save(updatedEntityMock);
        verify(objectMapper).readerForUpdating(existingMock);
        verify(objectReaderMock).readValue(any(JsonNode.class));
    }

    @Test
    public void testDeltaUpdateBatch() throws IOException {
        ObjectReader objectReaderMock = mock(ObjectReader.class);

        PlayerEntity existingEntityMock = mock(PlayerEntity.class);
        PlayerEntity updatedEntityMock = mock(PlayerEntity.class);
        Player existingMock = mock(Player.class);
        Player updatedMock = mock(Player.class);

        doReturn(Optional.of(existingEntityMock)).when(playerRepository).findById(any());
        doReturn(existingMock).when(playerMapper).entityToDto(existingEntityMock);
        doReturn(updatedEntityMock).when(playerMapper).dtoToEntity(any(Player.class));

        doReturn(objectReaderMock).when(objectMapper).readerForUpdating(existingMock);
        doReturn(mock(JsonNode.class)).when(objectMapper).convertValue(any(Player.class), eq(JsonNode.class));
        doReturn(updatedMock).when(objectReaderMock).readValue(any(JsonNode.class));

        playerService.deltaUpdateBatch(List.of(mock(Player.class), mock(Player.class)));

        verify(playerRepository).saveAll(anyCollection());
        verify(objectMapper, times(2)).readerForUpdating(existingMock);
        verify(objectReaderMock, times(2)).readValue(any(JsonNode.class));
    }
}
