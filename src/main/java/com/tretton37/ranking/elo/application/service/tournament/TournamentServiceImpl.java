package com.tretton37.ranking.elo.application.service.tournament;

import com.tretton37.ranking.elo.adapter.persistence.tournament.GroupGateway;
import com.tretton37.ranking.elo.adapter.persistence.tournament.TournamentGateway;
import com.tretton37.ranking.elo.adapter.persistence.tournament.match.TournamentMatchGatewayFacade;
import com.tretton37.ranking.elo.domain.model.exception.EntityNotFoundException;
import com.tretton37.ranking.elo.domain.model.exception.ErrorDetails;
import com.tretton37.ranking.elo.domain.model.tournament.Tournament;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.Bracket;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.BracketType;
import com.tretton37.ranking.elo.domain.model.tournament.group.Group;
import com.tretton37.ranking.elo.domain.model.tournament.group.GroupMatch;
import com.tretton37.ranking.elo.domain.service.tournament.TournamentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
@Slf4j
public class TournamentServiceImpl implements TournamentService {
    private final TournamentGateway gateway;
    private final TournamentMatchGatewayFacade matchGatewayFacade;
    private final GroupGateway groupGateway;

    @Autowired
    public TournamentServiceImpl(TournamentGateway gateway,
                                 TournamentMatchGatewayFacade matchGatewayFacade,
                                 GroupGateway groupGateway) {
        this.gateway = gateway;
        this.matchGatewayFacade = matchGatewayFacade;
        this.groupGateway = groupGateway;
    }

    @Override
    public Tournament getById(UUID id) {
        return gateway.getById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorDetails.ENTITY_NOT_FOUND,
                        "Tournament not found by Id: " + id));
    }

    @Override
    public Collection<Tournament> getAll() {
        return gateway.getAll();
    }

    @Override
    public Tournament persist(Tournament tournament) {
        return gateway.save(tournament);
    }

    @Override
    public Bracket getEliminationBracket(UUID tournamentId) {
        var matchStream = matchGatewayFacade.getEliminationGateway().findAllByTournamentId(tournamentId);

        return new Bracket(getById(tournamentId),
                matchStream.stream().filter(match -> match.getBracketType() == BracketType.UPPER).toList(),
                matchStream.stream().filter(match -> match.getBracketType() == BracketType.LOWER).toList(),
                matchStream.stream().filter(match -> match.getBracketType() == BracketType.GRAND_FINAL)
                        .findFirst()
                        .orElseThrow()
        );
    }

    @Override
    public Collection<Group> getGroups(UUID tournamentId) {
        return groupGateway.findAllByTournamentId(tournamentId);
    }

    @Override
    public Collection<GroupMatch> getGroupMatches(UUID tournamentId) {
        return matchGatewayFacade.getGroupGateway().findAllByTournamentId(tournamentId);
    }
}
