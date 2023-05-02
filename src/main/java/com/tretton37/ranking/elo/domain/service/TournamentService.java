package com.tretton37.ranking.elo.domain.service;

import com.tretton37.ranking.elo.domain.model.Tournament;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TournamentService {
    Mono<Tournament> getById(UUID id);
    Flux<Tournament> getAll();
    Mono<Tournament> create(Tournament tournament);
    void delete(UUID id);
}
