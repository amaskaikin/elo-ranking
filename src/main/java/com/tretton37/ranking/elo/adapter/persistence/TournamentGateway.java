package com.tretton37.ranking.elo.adapter.persistence;

import com.tretton37.ranking.elo.adapter.mappers.TournamentMapper;
import com.tretton37.ranking.elo.application.persistence.repository.TournamentRepository;
import com.tretton37.ranking.elo.domain.model.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TournamentGateway {

    private final TournamentRepository tournamentRepository;
    private final TournamentMapper mapper;

    @Autowired
    public TournamentGateway(TournamentRepository tournamentRepository,
                             TournamentMapper mapper) {
        this.tournamentRepository = tournamentRepository;
        this.mapper = mapper;
    }

    public Optional<Tournament> getById(UUID id) {
        return tournamentRepository.findById(id).flatMap(e -> Optional.ofNullable(mapper.entityToDto(e)));
    }

    public Collection<Tournament> getAll() {
        return tournamentRepository.findAll()
                .stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    public Tournament findByName(String name) {
        return mapper.entityToDto(tournamentRepository.findByName(name));
    }

    public Tournament save(Tournament tournament) {
        return mapper.entityToDto(tournamentRepository.save(mapper.dtoToEntity(tournament)));
    }

    public void delete(UUID id) {
        tournamentRepository.deleteById(id);
    }
}
