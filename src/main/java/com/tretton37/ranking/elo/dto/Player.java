package com.tretton37.ranking.elo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonMerge;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Player {
    private UUID id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String email;

    private String profileImage;

    @NotNull
    @JsonMerge
    private Tournament tournamentRef;

    private Integer rating;

    private LocalDateTime registeredWhen;

    private Integer gamesPlayed;

    @JsonIgnore
    private boolean reachedHighRating;
}
