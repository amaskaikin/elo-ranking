package com.tretton37.ranking.elo.domain.service.tournament.bracket;

import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.tournament.Tournament;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.Bracket;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.BracketSize;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.EliminationMatch;

import java.util.Collection;
import java.util.Comparator;

public class SingleEliminationBracketGenerator extends TournamentBracketGenerator {
    @Override
    public Bracket generate(Tournament tournament, Collection<Player> players) {
        this.bracketSize = BracketSize.getByValue(players.size());
        Bracket bracket = new Bracket(tournament);

        generateInitialRound(bracket, players);
        generateUpperBracket(bracket);
        generateFinalMatch(bracket);

        return bracket;
    }

    @Override
    protected void handleSeedMatch(EliminationMatch seedMatch) {
        winnersQueue.add(seedMatch);
    }

    @Override
    protected void handleUpperBracketMatch(EliminationMatch upperBracketMatch) {
        winnersQueue.add(upperBracketMatch);
    }

    private void generateFinalMatch(Bracket bracket) {
        var grandFinal = bracket.getUpper()
                .stream()
                .max(Comparator.comparing(EliminationMatch::getRound))
                .orElseThrow();

        bracket.getUpper().remove(grandFinal);
        bracket.setGrandFinal(grandFinal);
    }
}
