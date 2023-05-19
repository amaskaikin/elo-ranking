package com.tretton37.ranking.elo.application.service;

import com.tretton37.ranking.elo.adapter.persistence.LocationGateway;
import com.tretton37.ranking.elo.domain.model.Location;
import com.tretton37.ranking.elo.domain.model.exception.EntityAlreadyExistsException;
import com.tretton37.ranking.elo.domain.model.exception.EntityNotFoundException;
import com.tretton37.ranking.elo.domain.model.exception.ErrorDetails;
import com.tretton37.ranking.elo.domain.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
@Slf4j
public class LocationServiceImpl implements LocationService {

    private final LocationGateway locationGateway;

    @Autowired
    public LocationServiceImpl(LocationGateway locationGateway) {
        this.locationGateway = locationGateway;
    }

    @Override
    public Location getById(UUID id) {
        return locationGateway.getById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorDetails.ENTITY_NOT_FOUND,
                        "Location not found by Id: " + id));
    }

    @Override
    public Collection<Location> getAll() {
        return locationGateway.getAll();
    }

    @Override
    public Location create(Location location) {
        if (locationGateway.findByName(location.getName()) != null) {
            throw new EntityAlreadyExistsException(ErrorDetails.ENTITY_ALREADY_EXISTS,
                    "Location with the same name already exists");
        }

        return locationGateway.save(location);
    }

    @Override
    public void delete(UUID id) {
        log.info("delete: Deleting location: {}", id);
        locationGateway.delete(id);
    }
}
