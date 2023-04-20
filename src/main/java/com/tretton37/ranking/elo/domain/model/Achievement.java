package com.tretton37.ranking.elo.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Achievement {
    private UUID id;
    @NotNull
    private AchievementDef type;
    @NotNull
    private String name;
    private String description;
    private String icon;

    @JsonIgnore
    private Set<UUID> playerIds;
}
