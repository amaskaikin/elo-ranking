package com.tretton37.ranking.elo.domain.model.tournament.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tretton37.ranking.elo.domain.model.PlayerRef;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString
public class GroupRecord {
    private PlayerRef playerRef;
    private int won;
    private int lost;
    private int points;

    @JsonIgnore
    private UUID id;
    @JsonIgnore
    private UUID groupId;
}
