package com.tretton37.ranking.elo.domain.service;

import com.tretton37.ranking.elo.domain.model.Location;

import java.util.Collection;
import java.util.UUID;

public interface LocationService {
    Location getById(UUID id);
    Collection<Location> getAll();
    Location create(Location location);
    void delete(UUID id);
}
