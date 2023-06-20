package com.tretton37.ranking.elo.domain.model.tournament;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tretton37.ranking.elo.domain.model.Location;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.MatchType;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tournament {
    private UUID id;
    private String name;
    private Location location;
    private TournamentType type;
    private Boolean ongoing;
    private Integer groupSize;
    private Integer winnersThreshold;
    private MatchType eliminationMatchType;
    private MatchType grandFinalMatchType;
    @JsonIgnore
    private Stage stage;
}
