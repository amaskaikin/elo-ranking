package com.tretton37.ranking.elo.adapter.persistence.tournament.match;

import com.tretton37.ranking.elo.adapter.mappers.tournament.match.EliminationMatchMapper;
import com.tretton37.ranking.elo.adapter.mappers.tournament.match.TournamentMatchMapper;
import com.tretton37.ranking.elo.application.persistence.repository.tournament.TournamentMatchRepository;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.BracketType;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.EliminationMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
public class EliminationMatchGateway extends AbstractTournamentMatchGateway<EliminationMatch> {
    private final EliminationMatchMapper mapper;

    @Autowired
    public EliminationMatchGateway(TournamentMatchRepository repository,
                                   EliminationMatchMapper mapper) {
        super(repository);
        this.mapper = mapper;
    }

    @Override
    protected TournamentMatchMapper<EliminationMatch> getMapper() {
        return mapper;
    }

    @Override
    public Collection<EliminationMatch> findAllByTournamentId(UUID tournamentId) {
        return super.findAllByTournamentIdAndType(tournamentId,
                BracketType.UPPER, BracketType.LOWER, BracketType.GRAND_FINAL);
    }
}
