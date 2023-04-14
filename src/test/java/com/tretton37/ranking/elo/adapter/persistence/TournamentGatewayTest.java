package com.tretton37.ranking.elo.adapter.persistence;

import com.tretton37.ranking.elo.adapter.mappers.PersistenceMapper;
import com.tretton37.ranking.elo.application.persistence.entity.TournamentEntity;
import com.tretton37.ranking.elo.application.persistence.repository.TournamentRepository;
import com.tretton37.ranking.elo.domain.model.Tournament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TournamentGatewayTest {
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private PersistenceMapper<Tournament, TournamentEntity> mapper;

    @InjectMocks
    private TournamentGateway tournamentGateway;

    private UUID tournamentId;
    private String tournamentName;
    private Tournament tournamentMock;
    private TournamentEntity tournamentEntityMock;

    @BeforeEach
    public void setup() {
        tournamentId = UUID.randomUUID();
        tournamentName = "Test Tournament";
        tournamentMock = mock(Tournament.class);
        tournamentEntityMock = mock(TournamentEntity.class);
    }

    @Test
    public void testGetById() {
        doReturn(Optional.of(tournamentEntityMock)).when(tournamentRepository).findById(tournamentId);
        when(mapper.entityToDto(tournamentEntityMock)).thenReturn(tournamentMock);
        when(tournamentMock.getId()).thenReturn(tournamentId);
        when(tournamentMock.getName()).thenReturn(tournamentName);

        Optional<Tournament> resultOptional = tournamentGateway.getById(tournamentId);
        assertTrue(resultOptional.isPresent());

        Tournament result = resultOptional.get();
        assertEquals(tournamentMock, result);
        assertEquals(tournamentId, result.getId());
        assertEquals(tournamentName, result.getName());

        verify(tournamentRepository).findById(tournamentId);
        verify(mapper).entityToDto(tournamentEntityMock);
    }

    @Test
    public void testGetAll() {
        when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournamentEntityMock));
        when(mapper.entityToDto(tournamentEntityMock)).thenReturn(tournamentMock);

        Collection<Tournament> result = tournamentGateway.getAll();

        assertThat(result, hasSize(1));
        assertEquals(tournamentMock, result.iterator().next());

        verify(tournamentRepository).findAll();
    }

    @Test
    public void testDelete() {
        doNothing().when(tournamentRepository).deleteById(tournamentId);
        tournamentGateway.delete(tournamentId);

        verify(tournamentRepository).deleteById(tournamentId);
    }
}
