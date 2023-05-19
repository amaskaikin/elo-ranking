package com.tretton37.ranking.elo.adapter.persistence;

import com.tretton37.ranking.elo.adapter.mappers.LocationMapper;
import com.tretton37.ranking.elo.application.persistence.repository.LocationRepository;
import com.tretton37.ranking.elo.domain.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LocationGateway {

    private final LocationRepository locationRepository;
    private final LocationMapper mapper;

    @Autowired
    public LocationGateway(LocationRepository locationRepository,
                           LocationMapper mapper) {
        this.locationRepository = locationRepository;
        this.mapper = mapper;
    }

    public Optional<Location> getById(UUID id) {
        return locationRepository.findById(id).flatMap(e -> Optional.ofNullable(mapper.entityToDto(e)));
    }

    public Collection<Location> getAll() {
        return locationRepository.findAll()
                .stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    public Location findByName(String name) {
        return mapper.entityToDto(locationRepository.findByName(name));
    }

    public Location save(Location location) {
        return mapper.entityToDto(locationRepository.save(mapper.dtoToEntity(location)));
    }

    public void delete(UUID id) {
        locationRepository.deleteById(id);
    }
}
