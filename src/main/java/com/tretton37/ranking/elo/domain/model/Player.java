package com.tretton37.ranking.elo.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonMerge;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Player {
    private UUID id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String email;
    private JsonNullable<String> profileImage;
    @NotNull
    @JsonMerge
    private Tournament tournamentRef;
    @Valid
    private JsonNullable<Collection<Achievement>> achievements;
    private Integer rating;
    private Double winRate;
    private LocalDateTime registeredWhen;
    @Builder.Default
    private Integer gamesPlayed = 0;
    @Builder.Default
    private Integer gamesWon = 0;
    @JsonIgnore
    private boolean reachedHighRating;

    public void countGame(boolean win) {
        this.gamesPlayed += 1;
        if (win) {
            this.gamesWon += 1;
        }
    }
}
