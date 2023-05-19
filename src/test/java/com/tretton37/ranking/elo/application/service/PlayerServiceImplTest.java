package com.tretton37.ranking.elo.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.tretton37.ranking.elo.adapter.persistence.PlayerGateway;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.search.PlayerFilteringCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
public class PlayerServiceImplTest {
    @Mock
    private PlayerGateway playerGateway;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PlayerServiceImpl playerService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(playerService, "initialRank", 1000);
    }

    @Test
    public void testList() {
        PlayerFilteringCriteria filteringCriteria =
                new PlayerFilteringCriteria(UUID.randomUUID(), 1, null, null);

        playerService.list(filteringCriteria, Pageable.unpaged());

        verify(playerGateway).find(eq(filteringCriteria), any(Pageable.class));
    }

    @Test
    public void testFind() {
        PlayerFilteringCriteria filteringCriteria =
                new PlayerFilteringCriteria(UUID.randomUUID(), null, "test@mail.com", "An Ma");

        playerService.find(filteringCriteria);

        verify(playerGateway).find(eq(filteringCriteria));
    }

    @Test
    public void testFindById() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);

        when(playerGateway.findById(id)).thenReturn(Optional.of(player));

        Player result = playerService.findById(id);

        assertThat(result, equalTo(player));
        verify(playerGateway).findById(id);
    }

    @Test
    public void testCreate() {
        Player player = Player.builder().build();
        when(playerGateway.findByEmail(player.getEmail())).thenReturn(null);
        when(playerGateway.save(player)).thenReturn(player);

        Player createdPlayer = playerService.create(player);

        assertThat(createdPlayer, notNullValue());
        assertThat(createdPlayer.getRegisteredWhen(), notNullValue());
        assertEquals(1000, createdPlayer.getRating());
        verify(playerGateway).save(player);
    }

    @Test
    public void testBulkCreate() {
        List<Player> players = Collections.singletonList(mock(Player.class));

        playerService.bulkCreate(players);

        verify(playerGateway).saveAll(anyCollection());
    }

    @Test
    public void testDeltaUpdate() throws IOException {
        ObjectReader objectReaderMock = mock(ObjectReader.class);

        Player existingMock = mock(Player.class);
        Player mergedMock = mock(Player.class);

        doReturn(Optional.of(existingMock)).when(playerGateway).findById(any(UUID.class));

        doReturn(objectReaderMock).when(objectMapper).readerForUpdating(existingMock);
        doReturn(mock(JsonNode.class)).when(objectMapper).convertValue(any(Player.class), eq(JsonNode.class));
        doReturn(mergedMock).when(objectReaderMock).readValue(any(JsonNode.class));
        doReturn(mergedMock).when(playerGateway).save(mergedMock);

        Player updated = playerService.deltaUpdate(UUID.randomUUID(), Player.builder().build());

        assertEquals(mergedMock, updated);
        verify(playerGateway).save(mergedMock);
        verify(objectMapper).readerForUpdating(existingMock);
        verify(objectReaderMock).readValue(any(JsonNode.class));
    }

    @Test
    public void testDeltaUpdateBatch() throws IOException {
        ObjectReader objectReaderMock = mock(ObjectReader.class);

        Player existingMock = mock(Player.class);
        Player mergedMock = mock(Player.class);

        doReturn(Optional.of(existingMock)).when(playerGateway).findById(any());

        doReturn(objectReaderMock).when(objectMapper).readerForUpdating(existingMock);
        doReturn(mock(JsonNode.class)).when(objectMapper).convertValue(any(Player.class), eq(JsonNode.class));
        doReturn(mergedMock).when(objectReaderMock).readValue(any(JsonNode.class));

        playerService.deltaUpdateBatch(List.of(mock(Player.class), mock(Player.class)));

        verify(playerGateway).saveAll(anyCollection());
        verify(objectMapper, times(2)).readerForUpdating(existingMock);
        verify(objectReaderMock, times(2)).readValue(any(JsonNode.class));
    }
}
