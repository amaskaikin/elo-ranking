package com.tretton37.ranking.elo.application.service.tournament;

import com.tretton37.ranking.elo.adapter.persistence.GameGateway;
import com.tretton37.ranking.elo.adapter.persistence.tournament.GroupGateway;
import com.tretton37.ranking.elo.adapter.persistence.tournament.match.TournamentMatchGateway;
import com.tretton37.ranking.elo.adapter.persistence.tournament.match.TournamentMatchGatewayFacade;
import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.PlayerRef;
import com.tretton37.ranking.elo.domain.model.tournament.Stage;
import com.tretton37.ranking.elo.domain.model.tournament.Tournament;
import com.tretton37.ranking.elo.domain.model.tournament.TournamentMatch;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.Bracket;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.BracketType;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.EliminationMatch;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.MatchStatus;
import com.tretton37.ranking.elo.domain.model.tournament.group.Group;
import com.tretton37.ranking.elo.domain.model.tournament.group.GroupRecord;
import com.tretton37.ranking.elo.domain.service.tournament.TournamentHandler;
import com.tretton37.ranking.elo.domain.service.tournament.TournamentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class TournamentHandlerImpl implements TournamentHandler {
    private final TournamentService tournamentService;
    private final TournamentMatchGatewayFacade matchGatewayFacade;
    private final GameGateway gameGateway;
    private final GroupGateway groupGateway;

    @Autowired
    public TournamentHandlerImpl(TournamentService tournamentService,
                                 TournamentMatchGatewayFacade matchGatewayFacade,
                                 GameGateway gameGateway,
                                 GroupGateway groupGateway) {
        this.tournamentService = tournamentService;
        this.matchGatewayFacade = matchGatewayFacade;
        this.gameGateway = gameGateway;
        this.groupGateway = groupGateway;
    }

    // ToDo: Move logic for ELIMINATION and GROUP stages to separate classes
    @Override
    public void evaluate(Game game) {
        var tournamentDetails = game.getTournamentDetails();
        if (tournamentDetails == null || tournamentDetails.getTournamentId() == null) {
            log.debug("evaluate: No tournament is assigned to the game {}, do nothing", game.getId());
            return;
        }

        Tournament tournament = tournamentService.getById(tournamentDetails.getTournamentId());
        if (!tournament.getOngoing()) {
            throw new IllegalArgumentException("It's prohibited to assign the game to completed tournament");
        }

        if (Stage.ELIMINATION == tournament.getStage()) {
            trackGameWinner(game);
        }
        else if (Stage.GROUP == tournament.getStage()) {
            trackGroupResults(game);
        }
    }

    private void trackGroupResults(Game game) {
        Collection<Group> groups = groupGateway.findAllByTournamentId(game.getTournamentDetails().getTournamentId());
        var playerARecord = findGroupRecordByPlayer(groups, game.getPlayerScoreA().getPlayerRef());
        var playerBRecord = findGroupRecordByPlayer(groups, game.getPlayerScoreB().getPlayerRef());

        if (!playerARecord.getGroupId().equals(playerBRecord.getGroupId())) {
            throw new IllegalArgumentException("Couldn't count game: Players assigned to different groups");
        }

        if (playerARecord.getPlayerRef().getId().equals(game.getWinnerId())) {
            updateGroupRecord(playerARecord, true);
            updateGroupRecord(playerBRecord, false);
        }

        if (playerBRecord.getPlayerRef().getId().equals(game.getWinnerId())) {
            updateGroupRecord(playerBRecord, true);
            updateGroupRecord(playerARecord, false);
        }

        var scheduledMatch = getScheduledMatch(game, matchGatewayFacade.getGroupGateway());
        scheduledMatch.setWinnerId(game.getWinnerId());
        scheduledMatch.setStatus(MatchStatus.COMPLETED);
        linkGameToMatch(scheduledMatch, game);
        updateGameTournamentRelationship(game, scheduledMatch);
        matchGatewayFacade.getGroupGateway().save(scheduledMatch);

        groupGateway.saveAllRecords(playerARecord, playerBRecord);
    }

    private void trackGameWinner(Game game) {
        EliminationMatch scheduledMatch = getScheduledMatch(game, matchGatewayFacade.getEliminationGateway());
        linkGameToMatch(scheduledMatch, game);

        if (game.getWinnerId().equals(scheduledMatch.getPlayerA().getId())) {
            log.trace("trackGameWinner: Increase score for PlayerA");
            scheduledMatch.setPlayerAScore(scheduledMatch.getPlayerAScore() + 1);
        } else {
            log.trace("trackGameWinner: Increase score for PlayerB");
            scheduledMatch.setPlayerBScore(scheduledMatch.getPlayerBScore() + 1);
        }
        var winnerId = getMatchWinnerId(scheduledMatch);
        if (winnerId != null) {
            log.info("trackGameWinner: Setting match winner: {}", winnerId);
            updateEliminationBracket(scheduledMatch, winnerId);
            scheduledMatch.setStatus(MatchStatus.COMPLETED);
        }

        matchGatewayFacade.getEliminationGateway().save(scheduledMatch);
        updateGameTournamentRelationship(game, scheduledMatch);
    }

    private void updateEliminationBracket(EliminationMatch scheduledMatch, UUID winnerId) {
        Tournament tournament = tournamentService.getById(scheduledMatch.getTournamentId());
        if (Stage.ELIMINATION != tournament.getStage()) {
            throw new IllegalArgumentException("Tournament is not in elimination stage");
        }

        Bracket bracket = tournamentService.getEliminationBracket(scheduledMatch.getTournamentId());
        PlayerRef winner = getPlayer(scheduledMatch, playerRef -> winnerId.equals(playerRef.getId()));
        PlayerRef loser = getPlayer(scheduledMatch, playerRef -> !winnerId.equals(playerRef.getId()));

        scheduledMatch.setWinnerId(winnerId);
        scheduledMatch.setStatus(MatchStatus.COMPLETED);

        if (scheduledMatch.getBracketType() == BracketType.GRAND_FINAL) {
            log.info("updateEliminationBracket: Mark tournament {} as Completed", tournament.getId());
            tournament.setOngoing(false);
            tournamentService.persist(tournament);

            return;
        }
        // Find next match in both brackets and assign it to the winner
        assignPlayerToNextMatch(scheduledMatch, winner, bracket.getAllMatches());
        if (!CollectionUtils.isEmpty(bracket.getLower())) {
            assignPlayerToNextMatch(scheduledMatch, loser, bracket.getLowerWithGrandFinal());
        }
    }

    private void assignPlayerToNextMatch(EliminationMatch currentMatch, PlayerRef player,
                                         Collection<EliminationMatch> bracketMatches) {
        var nextMatch = findNextMatch(bracketMatches, currentMatch);
        if (nextMatch.getPlayerA() == null) {
            nextMatch.setPlayerA(player);
        } else if (nextMatch.getPlayerB() == null) {
            nextMatch.setPlayerB(player);
        } else {
            if (!player.getId().equals(currentMatch.getWinnerId())) {
                log.info("Player {} is eliminated", player.getId());
                return;
            }

            throw new RuntimeException("Players already assigned to next match " + nextMatch.getId());
        }

        matchGatewayFacade.getEliminationGateway().save(nextMatch);
    }

    private <T extends TournamentMatch> T getScheduledMatch(final Game game, TournamentMatchGateway<T> gateway) {
        var tournamentDetails = game.getTournamentDetails();
        if (tournamentDetails.getMatchId() != null) {
            T scheduledMatch = gateway.findById(tournamentDetails.getMatchId());
            if (!samePlayers(game, scheduledMatch)) {
                throw new IllegalArgumentException("Match " + scheduledMatch.getId()
                        + " is scheduled for another players!");
            }
            return scheduledMatch;
        }

        var scheduledMatches = gateway.findAllByTournamentId(tournamentDetails.getTournamentId());
        return scheduledMatches.stream()
                .filter(match -> MatchStatus.SCHEDULED == match.getStatus()
                        && samePlayers(game, match))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No scheduled match found"));
    }

    private boolean samePlayers(final Game game, TournamentMatch scheduledMatch) {
        if (scheduledMatch.getPlayerA() == null || scheduledMatch.getPlayerB() == null) {
            return false;
        }
        var participantsIds = Stream.of(game.getPlayerScoreA().getPlayerRef().getId(),
                        game.getPlayerScoreB().getPlayerRef().getId())
                .collect(Collectors.toSet());
        var scheduledMatchParticipantIds = Stream.of(scheduledMatch.getPlayerA().getId(),
                        scheduledMatch.getPlayerB().getId())
                .collect(Collectors.toSet());

        return participantsIds.equals(scheduledMatchParticipantIds);
    }

    private UUID getMatchWinnerId(EliminationMatch match) {
        switch (match.getMatchType()) {
            case BO_1 -> {
                if (match.getPlayerAScore() == 1) return match.getPlayerA().getId();
                if (match.getPlayerBScore() == 1) return match.getPlayerB().getId();
            }
            case BO_3 -> {
                if (match.getPlayerAScore() == 2) return match.getPlayerA().getId();
                if (match.getPlayerBScore() == 2) return match.getPlayerB().getId();
            }
            case BO_5 -> {
                if (match.getPlayerAScore() == 3) return match.getPlayerA().getId();
                if (match.getPlayerBScore() == 3) return match.getPlayerB().getId();
            }
        }
        return null;
    }

    private void updateGameTournamentRelationship(Game game, TournamentMatch scheduledMatch) {
        var tournamentDetails = game.getTournamentDetails();
        tournamentDetails.setMatchId(scheduledMatch.getId());

        gameGateway.save(game);
    }

    private PlayerRef getPlayer(EliminationMatch match, Predicate<PlayerRef> predicate) {
        return Stream.of(match.getPlayerA(), match.getPlayerB())
                .filter(predicate)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No players that satisfy given predicate found"));
    }

    private EliminationMatch findNextMatch(Collection<EliminationMatch> nextMatches, EliminationMatch currentMatch) {
        return nextMatches.stream()
                .filter(match -> MatchStatus.SCHEDULED == match.getStatus())
                .filter(match -> Optional.ofNullable(match.getPreviousPairIds()).orElse(Collections.emptyList())
                        .contains(currentMatch.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Couldn't find next match for match: "
                        + currentMatch.getId()));
    }

    private GroupRecord findGroupRecordByPlayer(Collection<Group> groups, final PlayerRef playerRef) {
        return groups.stream()
                .flatMap(group -> group.getRecords().stream())
                .filter(rec -> playerRef.getId().equals(rec.getPlayerRef().getId()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Player " + playerRef.getId()
                        + " has not been assigned to any Group within the current tournament"));
    }

    private void updateGroupRecord(GroupRecord record, boolean isWinner) {
        if (isWinner) {
            record.setWon(record.getWon() + 1);
            record.setPoints(record.getPoints() + 3); // ToDo: Move to configuration
        } else {
            record.setLost(record.getLost() + 1);
        }
    }

    private void linkGameToMatch(TournamentMatch match, Game game) {
        if (match.getPlayedGamesIds() == null) {
            match.setPlayedGamesIds(new ArrayList<>());
        }
        match.getPlayedGamesIds().add(game.getId());
    }
}
