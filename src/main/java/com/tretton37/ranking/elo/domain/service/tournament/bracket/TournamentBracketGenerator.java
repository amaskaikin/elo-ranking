package com.tretton37.ranking.elo.domain.service.tournament.bracket;

import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.PlayerRef;
import com.tretton37.ranking.elo.domain.model.tournament.Tournament;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.Bracket;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.BracketSize;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.EliminationMatch;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.stream.IntStream;

public abstract class TournamentBracketGenerator {
    protected final Queue<EliminationMatch> winnersQueue = new ArrayDeque<>();
    protected BracketSize bracketSize;

    public abstract Bracket generate(Tournament tournament, Collection<Player> players);

    protected abstract void handleSeedMatch(EliminationMatch seedMatch);
    protected abstract void handleUpperBracketMatch(EliminationMatch upperBracketMatch);

    protected void generateInitialRound(Bracket bracket, final Collection<Player> players) {
        IntStream.range(0, bracketSize.getValue() / 2)
                .mapToObj(i -> {
                    Pair<Player, Player> balancedPair = extractBalancedPair(players);
                    Player player1 = balancedPair.getLeft();
                    Player player2 = balancedPair.getRight();

                    EliminationMatch match = new EliminationMatch();
                    match.setId(UUID.randomUUID());
                    match.setPlayerA(PlayerRef.builder()
                            .id(player1.getId())
                            .name(player1.getName())
                            .build()
                    );
                    match.setPlayerB(PlayerRef.builder()
                            .id(player2.getId())
                            .name(player2.getName())
                            .build()
                    );
                    match.setRound(1);

                    return match;
                })
                .forEach(seedMatch -> {
                    handleSeedMatch(seedMatch);
                    bracket.addUpper(seedMatch);
                });
    }

    protected void generateUpperBracket(Bracket bracket) {
        while (winnersQueue.size() > 1) {
            EliminationMatch previousMatchA = winnersQueue.remove();
            EliminationMatch previousMatchB = winnersQueue.remove();

            EliminationMatch match = new EliminationMatch();
            match.setId(UUID.randomUUID());
            match.setPreviousPair(Pair.of(previousMatchA, previousMatchB));
            match.setRound(previousMatchA.getRound() + 1);

            handleUpperBracketMatch(match);

            bracket.addUpper(match);
        }
    }

    protected Pair<Player, Player> extractBalancedPair(Collection<Player> players) {
        int halfSize = players.size() / 2;
        var firstPart = new ArrayList<>(players.stream().toList().subList(0, halfSize));
        var secondPart = new ArrayList<>(players.stream().toList().subList(halfSize, players.size()));

        Player leader = firstPart.remove(0);
        Player follower = secondPart.remove(secondPart.size() - 1);

        players.removeAll(List.of(leader, follower));

        return Pair.of(leader, follower);
    }
}
