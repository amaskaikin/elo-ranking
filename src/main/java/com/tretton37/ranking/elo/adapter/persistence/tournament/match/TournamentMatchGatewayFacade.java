package com.tretton37.ranking.elo.adapter.persistence.tournament.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TournamentMatchGatewayFacade {
    private final EliminationMatchGateway eliminationGateway;
    private final GroupMatchGateway groupMatchGateway;

    @Autowired
    public TournamentMatchGatewayFacade(EliminationMatchGateway eliminationGateway,
                                        GroupMatchGateway groupMatchGateway) {
        this.eliminationGateway = eliminationGateway;
        this.groupMatchGateway = groupMatchGateway;
    }

    public EliminationMatchGateway getEliminationGateway() {
        return eliminationGateway;
    }

    public GroupMatchGateway getGroupGateway() {
        return groupMatchGateway;
    }

}
