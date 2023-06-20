package com.tretton37.ranking.elo.domain.service.tournament;

import com.tretton37.ranking.elo.domain.model.tournament.Tournament;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.Bracket;
import com.tretton37.ranking.elo.domain.model.tournament.group.Group;

import java.util.Collection;

public interface TournamentSeedingService {
    Collection<Group> seedGroups(Tournament tournament);
    Bracket seedElimination(Tournament tournament);
}
