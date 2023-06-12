package com.tretton37.ranking.elo.application.service.tournament;

import com.tretton37.ranking.elo.adapter.persistence.tournament.GroupGateway;
import com.tretton37.ranking.elo.adapter.persistence.tournament.TournamentGateway;
import com.tretton37.ranking.elo.adapter.persistence.tournament.match.TournamentMatchGatewayFacade;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.PlayerRef;
import com.tretton37.ranking.elo.domain.model.search.PlayerFilteringCriteria;
import com.tretton37.ranking.elo.domain.model.tournament.Stage;
import com.tretton37.ranking.elo.domain.model.tournament.Tournament;
import com.tretton37.ranking.elo.domain.model.tournament.TournamentType;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.Bracket;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.EliminationMatch;
import com.tretton37.ranking.elo.domain.model.tournament.group.Group;
import com.tretton37.ranking.elo.domain.model.tournament.group.GroupRecord;
import com.tretton37.ranking.elo.domain.service.PlayerService;
import com.tretton37.ranking.elo.domain.service.tournament.TournamentSeedingService;
import com.tretton37.ranking.elo.domain.service.tournament.bracket.DoubleEliminationBracketGenerator;
import com.tretton37.ranking.elo.domain.service.tournament.bracket.SingleEliminationBracketGenerator;
import com.tretton37.ranking.elo.domain.service.tournament.bracket.TournamentBracketGenerator;
import com.tretton37.ranking.elo.domain.service.tournament.group.GroupGenerator;
import com.tretton37.ranking.elo.domain.service.tournament.group.GroupScheduleGenerator;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TournamentSeedingServiceImpl implements TournamentSeedingService {
    private final PlayerService playerService;
    private final TournamentMatchGatewayFacade matchGatewayFacade;
    private final TournamentGateway tournamentGateway;
    private final GroupGateway groupGateway;

    @Autowired
    public TournamentSeedingServiceImpl(PlayerService playerService,
                                        TournamentGateway tournamentGateway,
                                        TournamentMatchGatewayFacade matchGatewayFacade,
                                        GroupGateway groupGateway) {
        this.playerService = playerService;
        this.tournamentGateway = tournamentGateway;
        this.matchGatewayFacade = matchGatewayFacade;
        this.groupGateway = groupGateway;
    }

    @Transactional
    @Override
    public Collection<Group> seedGroups(Tournament tournament) {
        validateGroupSeedingConditions(tournament);

        List<Player> registeredPlayers = getRegisteredPlayers(tournament);
        Collection<Group> groups = new GroupGenerator(registeredPlayers, tournament).createGroups();

        // At first create empty entities to keep record references by ids
        groupGateway.saveAll(groups.stream()
                .map(group -> {
                    var copy = new Group();
                    copy.setId(group.getId());
                    return copy;
                })
                .toList());
        groupGateway.saveAll(groups);

        var groupMatches = new GroupScheduleGenerator(groups).generateRoundRobin();
        matchGatewayFacade.getGroupGateway().saveAll(groupMatches);

        tournament.setStage(Stage.GROUP);
        tournamentGateway.save(tournament);

        return groups;
    }

    @Transactional
    @Override
    public Bracket seedElimination(Tournament tournament) {
        if (!CollectionUtils.isEmpty(matchGatewayFacade.getEliminationGateway()
                .findAllByTournamentId(tournament.getId()))) {
            throw new IllegalArgumentException("Bracket is already seeded for tournament: " + tournament.getId());
        }
        List<Player> registeredPlayers = getRegisteredPlayers(tournament);
        Bracket bracket = getBracketGenerator(tournament).generate(tournament, registeredPlayers);
        saveBracket(bracket);

        tournament.setStage(Stage.ELIMINATION);
        tournamentGateway.save(tournament);

        return bracket;
    }

    private void saveBracket(Bracket bracket) {
        var matches = Stream.concat(Stream.of(bracket.getGrandFinal()),
                        Stream.concat(bracket.getUpper().stream(), bracket.getLower().stream()))
                .toList();

        // Create empty entities to keep references by ids
        matchGatewayFacade.getEliminationGateway().saveAll(matches.stream()
                .map(match -> {
                    var copy = new EliminationMatch();
                    copy.setId(match.getId());
                    return copy;
                })
                .toList());
        // Update links to previous matches
        matchGatewayFacade.getEliminationGateway().saveAll(matches);
    }

    private List<Player> getRegisteredPlayers(Tournament tournament) {
        if (Stage.GROUP == tournament.getStage()) {
            var promotedPlayers = groupGateway.findAllByTournamentId(tournament.getId())
                    .stream()
                    .flatMap(group -> group.getRecords().stream()
                            .limit(tournament.getWinnersThreshold())
                            .map(GroupRecord::getPlayerRef)
                            .map(PlayerRef::getId))
                    .collect(Collectors.toSet());

            return new ArrayList<>(playerService.findAllByIds(promotedPlayers));
        }
        PlayerFilteringCriteria playersFilter = new PlayerFilteringCriteria();
        playersFilter.setTournamentId(tournament.getId());

        return new ArrayList<>(playerService.find(playersFilter));
    }

    private TournamentBracketGenerator getBracketGenerator(Tournament tournament) {
        return switch (tournament.getType()) {
            case DOUBLE_ONLY, GROUP_DOUBLE -> new DoubleEliminationBracketGenerator();
            case SINGLE_ONLY, GROUP_SINGLE -> new SingleEliminationBracketGenerator();
        };
    }

    private void validateGroupSeedingConditions(Tournament tournament) {
        if (!CollectionUtils.isEmpty(groupGateway.findAllByTournamentId(tournament.getId()))) {
            throw new IllegalArgumentException("Groups are already seeded for tournament: " + tournament.getId());
        }
        if (TournamentType.GROUP_DOUBLE != tournament.getType()
                && TournamentType.GROUP_SINGLE != tournament.getType()) {
            throw new IllegalArgumentException("Incorrect tournament type");
        }
        if (ObjectUtils.anyNull(tournament.getGroupSize(), tournament.getWinnersThreshold())) {
            throw new IllegalArgumentException("groupSize and winnersThreshold are mandatory for Group Stage");
        }
    }
}
