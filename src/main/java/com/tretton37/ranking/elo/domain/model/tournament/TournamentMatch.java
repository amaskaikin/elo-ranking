package com.tretton37.ranking.elo.domain.model.tournament;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tretton37.ranking.elo.domain.model.PlayerRef;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.BracketType;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.MatchStatus;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.MatchType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.UUID;

@Data
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TournamentMatch {
    private UUID id;
    @JsonIgnore
    private PlayerRef playerA;
    @JsonIgnore
    private PlayerRef playerB;
    private UUID winnerId;
    private UUID tournamentId;
    private MatchStatus status;
    private MatchType matchType;
    private BracketType bracketType;
    private Collection<UUID> playedGamesIds;
}
