package com.tretton37.ranking.elo.domain.model.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerFilteringCriteria {
    private UUID locationId;
    private Integer gamesPlayed;
    private String email;
    private String name;
    private UUID tournamentId;
}
