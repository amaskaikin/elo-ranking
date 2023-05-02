package com.tretton37.ranking.elo.adapter.persistence;

import com.tretton37.ranking.elo.adapter.mappers.PersistenceMapper;
import com.tretton37.ranking.elo.application.persistence.entity.TournamentEntity;
import com.tretton37.ranking.elo.application.persistence.repository.TournamentRepository;
import com.tretton37.ranking.elo.domain.model.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
public class TournamentGateway {

    private final TournamentRepository tournamentRepository;
    private final PersistenceMapper<Tournament, TournamentEntity> mapper;

    @Autowired
    public TournamentGateway(TournamentRepository tournamentRepository,
                             PersistenceMapper<Tournament, TournamentEntity> mapper) {
        this.tournamentRepository = tournamentRepository;
        this.mapper = mapper;
    }

    public Mono<Tournament> getById(final UUID id) {
        return Mono.fromCallable(() -> tournamentRepository.findById(id))
                .flatMap(o -> o.map(e -> Mono.just(mapper.entityToDto(e)))
                        .orElseGet(Mono::empty)
                )
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<Tournament> getAll() {
        return Mono.fromCallable(tournamentRepository::findAll)
                .flatMapMany(e -> Flux.fromStream(e.stream().map(mapper::entityToDto)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Tournament> findByName(String name) {
        return Mono.fromCallable(() -> tournamentRepository.findByName(name))
                .map(mapper::entityToDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Tournament> save(Tournament tournament) {
        return Mono.fromCallable(() -> tournamentRepository.save(mapper.dtoToEntity(tournament)))
                .map(mapper::entityToDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public void delete(UUID id) {
        Mono.fromCallable(() -> {
            tournamentRepository.deleteById(id);
            return Mono.empty();
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
