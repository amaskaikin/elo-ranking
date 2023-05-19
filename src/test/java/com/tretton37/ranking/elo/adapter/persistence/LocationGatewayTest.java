package com.tretton37.ranking.elo.adapter.persistence;

import com.tretton37.ranking.elo.adapter.mappers.LocationMapper;
import com.tretton37.ranking.elo.application.persistence.entity.LocationEntity;
import com.tretton37.ranking.elo.application.persistence.repository.LocationRepository;
import com.tretton37.ranking.elo.domain.model.Location;
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
public class LocationGatewayTest {
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private LocationMapper mapper;

    @InjectMocks
    private LocationGateway locationGateway;

    private UUID locationId;
    private String locationName;
    private Location locationMock;
    private LocationEntity locationEntityMock;

    @BeforeEach
    public void setup() {
        locationId = UUID.randomUUID();
        locationName = "Test Location";
        locationMock = mock(Location.class);
        locationEntityMock = mock(LocationEntity.class);
    }

    @Test
    public void testGetById() {
        doReturn(Optional.of(locationEntityMock)).when(locationRepository).findById(locationId);
        when(mapper.entityToDto(locationEntityMock)).thenReturn(locationMock);
        when(locationMock.getId()).thenReturn(locationId);
        when(locationMock.getName()).thenReturn(locationName);

        Optional<Location> resultOptional = locationGateway.getById(locationId);
        assertTrue(resultOptional.isPresent());

        Location result = resultOptional.get();
        assertEquals(locationMock, result);
        assertEquals(locationId, result.getId());
        assertEquals(locationName, result.getName());

        verify(locationRepository).findById(locationId);
        verify(mapper).entityToDto(locationEntityMock);
    }

    @Test
    public void testGetAll() {
        when(locationRepository.findAll()).thenReturn(Collections.singletonList(locationEntityMock));
        when(mapper.entityToDto(locationEntityMock)).thenReturn(locationMock);

        Collection<Location> result = locationGateway.getAll();

        assertThat(result, hasSize(1));
        assertEquals(locationMock, result.iterator().next());

        verify(locationRepository).findAll();
    }

    @Test
    public void testDelete() {
        doNothing().when(locationRepository).deleteById(locationId);
        locationGateway.delete(locationId);

        verify(locationRepository).deleteById(locationId);
    }
}
