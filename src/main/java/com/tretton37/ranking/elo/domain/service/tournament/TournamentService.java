package com.tretton37.ranking.elo.domain.service.tournament;

import com.tretton37.ranking.elo.domain.model.tournament.Tournament;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.Bracket;
import com.tretton37.ranking.elo.domain.model.tournament.group.Group;
import com.tretton37.ranking.elo.domain.model.tournament.group.GroupMatch;

import java.util.Collection;
import java.util.UUID;

public interface TournamentService {
    Tournament getById(UUID id);
    Collection<Tournament> getAll();
    Tournament persist(Tournament tournament);
    Bracket getEliminationBracket(UUID tournamentId);
    Collection<Group> getGroups(UUID tournamentId);
    Collection<GroupMatch> getGroupMatches(UUID tournamentId);
}
