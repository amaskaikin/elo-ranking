package com.tretton37.ranking.elo.domain.model.tournament.bracket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tretton37.ranking.elo.domain.model.tournament.Tournament;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;

@ToString
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Bracket {
    @JsonIgnore
    private final Tournament tournament;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final Collection<EliminationMatch> upper;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final Collection<EliminationMatch> lower;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private EliminationMatch grandFinal;

    public Bracket(Tournament tournament) {
        this.tournament = tournament;
        this.upper = new ArrayList<>();
        this.lower = new ArrayList<>();
    }

    public Bracket(Tournament tournament, Collection<EliminationMatch> upper,
                   Collection<EliminationMatch> lower,
                   EliminationMatch grandFinal) {
        this.tournament = tournament;
        this.upper = upper;
        this.lower = lower;
        this.grandFinal = grandFinal;
    }

    public void setGrandFinal(EliminationMatch grandFinal) {
        this.grandFinal = grandFinal;

        configureDefaults(this.grandFinal);
        this.grandFinal.setBracketType(BracketType.GRAND_FINAL);
        this.grandFinal.setMatchType(tournament.getGrandFinalMatchType());
    }

    public void addUpper(EliminationMatch match) {
        configureDefaults(match);
        match.setBracketType(BracketType.UPPER);

        this.upper.add(match);
    }

    public void addLower(EliminationMatch match) {
        configureDefaults(match);
        match.setBracketType(BracketType.LOWER);

        this.lower.add(match);
    }

    @JsonIgnore
    public Collection<EliminationMatch> getAllMatches() {
        var result = new ArrayList<>(upper);
        result.addAll(lower);
        result.add(grandFinal);

        return result;
    }

    @JsonIgnore
    public Collection<EliminationMatch> getLowerWithGrandFinal() {
        var result = new ArrayList<>(lower);
        result.add(grandFinal);

        return result;
    }

    private void configureDefaults(EliminationMatch match) {
        match.setTournamentId(tournament.getId());
        match.setMatchType(tournament.getEliminationMatchType());
        if (match.getStatus() == null) {
            match.setStatus(MatchStatus.SCHEDULED);
        }
    }
}