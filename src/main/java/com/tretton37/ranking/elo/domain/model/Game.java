package com.tretton37.ranking.elo.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Game {
    private UUID id;
    @NotNull
    private PlayerScore playerScoreA;
    @NotNull
    private PlayerScore playerScoreB;
    @NotNull
    private Location locationRef;
    private UUID winnerId;
    private LocalDateTime playedWhen;
    private TournamentDetails tournamentDetails;

    @JsonIgnore
    private UUID gameResultId;

    @JsonIgnore
    public UUID getPlayerIdA() {
        return playerScoreA.getPlayerRef().getId();
    }

    @JsonIgnore
    public UUID getPlayerIdB() {
        return playerScoreB.getPlayerRef().getId();
    }

    @Data
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TournamentDetails {
        private UUID tournamentId;
        private UUID matchId;
    }
}
