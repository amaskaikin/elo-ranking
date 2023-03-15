package com.tretton37.ranking.elo.service;

import com.tretton37.ranking.elo.dto.Tournament;
import com.tretton37.ranking.elo.dto.mapper.PersistenceMapper;
import com.tretton37.ranking.elo.errorhandling.EntityAlreadyExistsException;
import com.tretton37.ranking.elo.persistence.TournamentRepository;
import com.tretton37.ranking.elo.persistence.entity.TournamentEntity;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private PersistenceMapper<Tournament, TournamentEntity> mapper;

    @InjectMocks
    private TournamentService tournamentService;

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
    public void testGetTournamentById() {
        doReturn(Optional.of(tournamentEntityMock)).when(tournamentRepository).findById(tournamentId);
        when(mapper.entityToDto(tournamentEntityMock)).thenReturn(tournamentMock);
        when(tournamentMock.getId()).thenReturn(tournamentId);
        when(tournamentMock.getName()).thenReturn(tournamentName);

        Tournament result = tournamentService.getTournamentById(tournamentId);

        assertEquals(tournamentMock, result);
        assertEquals(tournamentId, result.getId());
        assertEquals(tournamentName, result.getName());

        verify(tournamentRepository).findById(tournamentId);
        verify(mapper).entityToDto(tournamentEntityMock);
    }

    @Test
    public void testGetAllTournaments() {
        when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournamentEntityMock));
        when(mapper.entityToDto(tournamentEntityMock)).thenReturn(tournamentMock);

        Collection<Tournament> result = tournamentService.getAllTournaments();

        assertThat(result, hasSize(1));
        assertEquals(tournamentMock, result.iterator().next());

        verify(tournamentRepository).findAll();
    }

    @Test
    public void testCreateTournament() {
        doReturn(null).when(tournamentRepository).findByName(tournamentName);
        doReturn(tournamentEntityMock).when(tournamentRepository).save(tournamentEntityMock);
        doReturn(tournamentMock).when(mapper).entityToDto(tournamentEntityMock);
        doReturn(tournamentEntityMock).when(mapper).dtoToEntity(tournamentMock);
        when(tournamentMock.getName()).thenReturn(tournamentName);

        Tournament result = tournamentService.createTournament(tournamentMock);

        assertEquals(tournamentMock, result);
        verify(tournamentRepository).save(tournamentEntityMock);
    }

    @Test
    public void testCreateTournament_shouldThrowException_whenSameNameIsUsed() {
        when(tournamentMock.getName()).thenReturn(tournamentName);
        when(tournamentRepository.findByName(tournamentName))
                .thenReturn(tournamentEntityMock);

        assertThrows(EntityAlreadyExistsException.class,
                () -> tournamentService.createTournament(tournamentMock));
    }

    @Test
    public void testDeleteTournament() {
        doNothing().when(tournamentRepository).deleteById(tournamentId);
        tournamentService.deleteTournament(tournamentId);

        verify(tournamentRepository).deleteById(tournamentId);
    }
}
