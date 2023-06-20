package com.tretton37.ranking.elo.adapter.persistence.tournament.match;

import com.tretton37.ranking.elo.adapter.mappers.tournament.match.TournamentMatchMapper;
import com.tretton37.ranking.elo.application.persistence.repository.tournament.TournamentMatchRepository;
import com.tretton37.ranking.elo.domain.model.exception.EntityNotFoundException;
import com.tretton37.ranking.elo.domain.model.exception.ErrorDetails;
import com.tretton37.ranking.elo.domain.model.tournament.TournamentMatch;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.BracketType;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractTournamentMatchGateway<T extends TournamentMatch> implements TournamentMatchGateway<T> {
    private final TournamentMatchRepository repository;

    protected AbstractTournamentMatchGateway(TournamentMatchRepository repository) {
        this.repository = repository;
    }

    protected abstract TournamentMatchMapper<T> getMapper();

    protected Collection<T> findAllByTournamentIdAndType(UUID tournamentId, BracketType... types) {
        return repository.findAllByTournamentIdAndBracketTypeIn(tournamentId, types).stream()
                .map(e -> getMapper().toDto(e))
                .collect(Collectors.toList());
    }

    @Override
    public T findById(UUID id) {
        return repository.findById(id)
                .map(e -> getMapper().toDto(e))
                .orElseThrow(() -> new EntityNotFoundException(ErrorDetails.ENTITY_NOT_FOUND,
                        "Couldn't find match by id: " + id));
    }

    @Override
    public T save(T match) {
        return getMapper().toDto(repository.save(getMapper().toEntity(match)));
    }

    @Override
    public void saveAll(Collection<T> matches) {
        repository.saveAll(matches.stream()
                .map(e -> getMapper().toEntity(e))
                .toList());
    }
}
