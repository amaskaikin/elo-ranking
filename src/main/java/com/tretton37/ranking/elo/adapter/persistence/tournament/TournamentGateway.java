package com.tretton37.ranking.elo.adapter.persistence.tournament;

import com.tretton37.ranking.elo.adapter.mappers.tournament.TournamentMapper;
import com.tretton37.ranking.elo.application.persistence.repository.tournament.TournamentRepository;
import com.tretton37.ranking.elo.domain.model.tournament.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TournamentGateway {
    private final TournamentRepository repository;
    private final TournamentMapper mapper;

    @Autowired
    public TournamentGateway(TournamentRepository repository, TournamentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Optional<Tournament> getById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDto);
    }

    public Collection<Tournament> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public Tournament save(Tournament tournament) {
        return mapper.toDto(repository.save(mapper.toEntity(tournament)));
    }
}
