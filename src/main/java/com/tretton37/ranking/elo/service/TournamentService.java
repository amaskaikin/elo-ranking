package com.tretton37.ranking.elo.service;

import com.tretton37.ranking.elo.dto.Tournament;
import com.tretton37.ranking.elo.dto.mapper.PersistenceMapper;
import com.tretton37.ranking.elo.errorhandling.EntityAlreadyExistsException;
import com.tretton37.ranking.elo.errorhandling.EntityNotFoundException;
import com.tretton37.ranking.elo.errorhandling.ErrorDetails;
import com.tretton37.ranking.elo.persistence.TournamentRepository;
import com.tretton37.ranking.elo.persistence.entity.TournamentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final PersistenceMapper<Tournament, TournamentEntity> mapper;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository,
                             PersistenceMapper<Tournament, TournamentEntity> mapper) {
        this.tournamentRepository = tournamentRepository;
        this.mapper = mapper;
    }

    public Tournament getTournamentById(UUID id) {
        TournamentEntity entity = tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorDetails.ENTITY_NOT_FOUND,
                        "Tournament not found by Id: " + id));
        return mapper.entityToDto(entity);
    }

    public Collection<Tournament> getAllTournaments() {
        return tournamentRepository.findAll()
                .stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    public Tournament createTournament(Tournament tournament) {
        if (tournamentRepository.findByName(tournament.name()) != null) {
            throw new EntityAlreadyExistsException(ErrorDetails.ENTITY_ALREADY_EXISTS,
                    "Tournament with the same name already exists");
        }
        return mapper.entityToDto(tournamentRepository.save(mapper.dtoToEntity(tournament)));
    }
}
