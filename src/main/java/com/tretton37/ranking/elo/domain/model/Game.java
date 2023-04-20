package com.tretton37.ranking.elo.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Game {
    private UUID id;

    @NotNull
    private PlayerScore playerScoreA;

    @NotNull
    private PlayerScore playerScoreB;

    @NotNull
    private Tournament tournamentRef;

    private UUID winnerId;

    private LocalDateTime playedWhen;

}
