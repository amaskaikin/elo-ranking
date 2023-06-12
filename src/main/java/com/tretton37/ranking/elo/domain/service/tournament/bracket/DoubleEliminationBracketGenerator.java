package com.tretton37.ranking.elo.domain.service.tournament.bracket;

import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.tournament.Tournament;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.Bracket;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.BracketSize;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.EliminationMatch;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import java.util.UUID;

public class DoubleEliminationBracketGenerator extends TournamentBracketGenerator {
    private final Queue<EliminationMatch> losersQueue = new ArrayDeque<>();
    private final Queue<EliminationMatch> backfillQueue = new ArrayDeque<>();

    @Override
    public Bracket generate(Tournament tournament, Collection<Player> players) {
        super.bracketSize = BracketSize.getByValue(players.size());
        Bracket bracket = new Bracket(tournament);

        generateInitialRound(bracket, players);
        generateUpperBracket(bracket);
        generateLowerBracket(bracket);
        generateFinalMatch(bracket);

        return bracket;
    }

    @Override
    protected void handleSeedMatch(EliminationMatch seedMatch) {
        winnersQueue.add(seedMatch);
        losersQueue.add(seedMatch);
    }

    @Override
    protected void handleUpperBracketMatch(EliminationMatch upperBracketMatch) {
        winnersQueue.add(upperBracketMatch);
        backfillQueue.add(upperBracketMatch);
    }

    private void generateLowerBracket(Bracket bracket) {
        // The current pattern alternates between pairing consecutive matches and pairing with backfill matches.
        // The switched pattern pairs matches with backfill matches instead of consecutive matches.
        boolean switchFlag = false;
        int roundSwitch = bracketSize.getValue() / 2;

        int counter = 0; // Counts the number of matches processed in the current round
        int switchedCounter = 0; // Counts the number of matches processed in the switched pattern

        while (!losersQueue.isEmpty() && !backfillQueue.isEmpty()) {
            EliminationMatch eliminationMatch1 = losersQueue.remove();
            EliminationMatch eliminationMatch2;

            if (switchFlag) {
                switchedCounter += 2;
                eliminationMatch2 = backfillQueue.remove();

                if (switchedCounter == roundSwitch) {
                    roundSwitch /= 2;
                    switchFlag = false;
                    switchedCounter = 0;
                }
            } else {
                eliminationMatch2 = losersQueue.remove();
                counter += 2;

                if (counter == roundSwitch) {
                    switchFlag = true;
                    counter = 0;
                }
            }

            EliminationMatch eliminationMatchLower = new EliminationMatch();
            eliminationMatchLower.setId(UUID.randomUUID());
            eliminationMatchLower.setPreviousPair(Pair.of(eliminationMatch1, eliminationMatch2));
            eliminationMatchLower.setRound(eliminationMatch1.getRound() + 1);

            losersQueue.add(eliminationMatchLower);
            bracket.addLower(eliminationMatchLower);
        }
    }

    private void generateFinalMatch(Bracket bracket) {
        EliminationMatch finalMatch = new EliminationMatch();
        finalMatch.setId(UUID.randomUUID());
        finalMatch.setPreviousPair(Pair.of(winnersQueue.remove(), losersQueue.remove()));

        bracket.setGrandFinal(finalMatch);
    }
}
