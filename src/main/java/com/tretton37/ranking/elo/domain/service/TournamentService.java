package com.tretton37.ranking.elo.domain.service;

import com.tretton37.ranking.elo.domain.model.Tournament;

import java.util.Collection;
import java.util.UUID;

public interface TournamentService {
    Tournament getById(UUID id);
    Collection<Tournament> getAll();
    Tournament create(Tournament tournament);
    void delete(UUID id);
}
