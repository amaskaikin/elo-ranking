package com.tretton37.ranking.elo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@ToString
public class Player {
    private UUID id;

    @NotEmpty
    private String name;

    private Integer rating;

    private LocalDateTime registeredWhen;

    private Integer gamesPlayed;
}
