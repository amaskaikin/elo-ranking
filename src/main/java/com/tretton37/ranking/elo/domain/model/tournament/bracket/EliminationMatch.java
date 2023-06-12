package com.tretton37.ranking.elo.domain.model.tournament.bracket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tretton37.ranking.elo.domain.model.PlayerScore;
import com.tretton37.ranking.elo.domain.model.tournament.TournamentMatch;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EliminationMatch extends TournamentMatch {
    @JsonIgnore
    private int playerAScore;
    @JsonIgnore
    private int playerBScore;
    private Integer round;

    @JsonIgnore
    private Pair<EliminationMatch, EliminationMatch> previousPair = null;

    @JsonProperty("previousPairMatchesIds")
    public Collection<UUID> getPreviousPairIds() {
        return previousPair == null || previousPair.getLeft() == null || previousPair.getRight() == null
                ? null : List.of(previousPair.getLeft().getId(), previousPair.getRight().getId());
    }

    @JsonProperty("playerScoreA")
    public PlayerScore getPlayerScoreA() {
        var score = new PlayerScore();
        score.setPlayerRef(getPlayerA());
        score.setScore(playerAScore);

        return score;
    }

    @JsonProperty("playerScoreB")
    public PlayerScore getPlayerScoreB() {
        var score = new PlayerScore();
        score.setPlayerRef(getPlayerB());
        score.setScore(playerBScore);

        return score;
    }
}
