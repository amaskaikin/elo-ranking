package com.tretton37.ranking.elo.domain.service.tournament.group;

import com.tretton37.ranking.elo.domain.model.PlayerRef;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.BracketType;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.MatchStatus;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.MatchType;
import com.tretton37.ranking.elo.domain.model.tournament.group.Group;
import com.tretton37.ranking.elo.domain.model.tournament.group.GroupMatch;
import com.tretton37.ranking.elo.domain.model.tournament.group.GroupRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class GroupScheduleGenerator {
    private final Collection<Group> groups;

    public GroupScheduleGenerator(Collection<Group> groups) {
        this.groups = groups;
    }

    public Collection<GroupMatch> generateRoundRobin() {
        List<GroupMatch> schedule = new ArrayList<>();

        for (Group group : groups) {
            List<PlayerRef> players = new ArrayList<>();
            group.getRecords().stream()
                    .map(GroupRecord::getPlayerRef)
                    .forEach(players::add);

            if (players.size() % 2 != 0) {
                throw new UnsupportedOperationException("Cannot generate round-robin schedule " +
                        "with odd number of players");
            }

            int numPlayers = players.size();
            int numRounds = numPlayers - 1;

            for (int round = 0; round < numRounds; round++) {
                for (int i = 0; i < numPlayers / 2; i++) {
                    int playerIndexA = (round + i) % (numPlayers - 1);
                    int playerIndexB = (round + numPlayers - i) % (numPlayers - 1);

                    PlayerRef playerA = players.get(playerIndexA);
                    PlayerRef playerB = players.get(playerIndexB);

                    GroupMatch match = new GroupMatch();
                    match.setId(UUID.randomUUID());
                    match.setPlayerA(playerA);
                    match.setPlayerB(playerB);
                    match.setTournamentId(group.getTournamentId());
                    match.setGroupId(group.getId());
                    match.setStatus(MatchStatus.SCHEDULED);
                    match.setMatchType(MatchType.BO_1);
                    match.setBracketType(BracketType.GROUP);

                    schedule.add(match);
                }
            }
        }

        return schedule;
    }
}
