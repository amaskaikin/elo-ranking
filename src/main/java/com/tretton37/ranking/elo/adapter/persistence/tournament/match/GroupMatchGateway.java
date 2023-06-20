package com.tretton37.ranking.elo.adapter.persistence.tournament.match;

import com.tretton37.ranking.elo.adapter.mappers.tournament.match.GroupMatchMapper;
import com.tretton37.ranking.elo.adapter.mappers.tournament.match.TournamentMatchMapper;
import com.tretton37.ranking.elo.application.persistence.repository.tournament.TournamentMatchRepository;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.BracketType;
import com.tretton37.ranking.elo.domain.model.tournament.group.GroupMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
public class GroupMatchGateway extends AbstractTournamentMatchGateway<GroupMatch> {
    private final GroupMatchMapper mapper;

    @Autowired
    public GroupMatchGateway(TournamentMatchRepository repository,
                             GroupMatchMapper mapper) {
        super(repository);
        this.mapper = mapper;
    }

    @Override
    protected TournamentMatchMapper<GroupMatch> getMapper() {
        return mapper;
    }

    @Override
    public Collection<GroupMatch> findAllByTournamentId(UUID tournamentId) {
        return super.findAllByTournamentIdAndType(tournamentId, BracketType.GROUP);
    }
}
