package com.tretton37.ranking.elo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tretton37.ranking.elo.persistence.entity.GameResult;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@ToString
public class Game {
    private UUID id;

    @NotNull
    private Player playerA;

    @NotNull
    private Player playerB;

    @JsonProperty(required = true)
    @NotNull
    private GameResult result;

    private LocalDateTime playedWhen;
}
