package com.tretton37.ranking.elo.dto;

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
    private PlayerRef playerRefA;

    @NotNull
    private PlayerRef playerRefB;

    @NotNull
    private Tournament tournamentRef;

    @NotNull
    private GameResult gameResult;

    private LocalDateTime playedWhen;

    @Data
    @Builder
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GameResult {
        private Integer playerAScore;
        private Integer playerBScore;
        private Integer playerARatingAlteration;
        private Integer playerBRatingAlteration;
        private UUID winnerId;
    }
}
