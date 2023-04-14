package com.tretton37.ranking.elo.application.service;

import com.tretton37.ranking.elo.adapter.persistence.TournamentGateway;
import com.tretton37.ranking.elo.domain.model.Tournament;
import com.tretton37.ranking.elo.domain.model.exception.EntityAlreadyExistsException;
import com.tretton37.ranking.elo.domain.model.exception.EntityNotFoundException;
import com.tretton37.ranking.elo.domain.model.exception.ErrorDetails;
import com.tretton37.ranking.elo.domain.service.TournamentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
@Slf4j
public class TournamentServiceImpl implements TournamentService {

    private final TournamentGateway tournamentGateway;

    @Autowired
    public TournamentServiceImpl(TournamentGateway tournamentGateway) {
        this.tournamentGateway = tournamentGateway;
    }

    @Override
    public Tournament getById(UUID id) {
        return tournamentGateway.getById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorDetails.ENTITY_NOT_FOUND,
                        "Tournament not found by Id: " + id));
    }

    @Override
    public Collection<Tournament> getAll() {
        return tournamentGateway.getAll();
    }

    @Override
    public Tournament create(Tournament tournament) {
        if (tournamentGateway.findByName(tournament.getName()) != null) {
            throw new EntityAlreadyExistsException(ErrorDetails.ENTITY_ALREADY_EXISTS,
                    "Tournament with the same name already exists");
        }

        return tournamentGateway.save(tournament);
    }

    @Override
    public void delete(UUID id) {
        log.info("delete: Deleting tournament: {}", id);
        tournamentGateway.delete(id);
    }
}
