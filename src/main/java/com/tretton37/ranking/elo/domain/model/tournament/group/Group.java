package com.tretton37.ranking.elo.domain.model.tournament.group;

import lombok.Data;

import java.util.Collection;
import java.util.UUID;

@Data
public class Group {
    private UUID id;
    private String name;
    private UUID tournamentId;
    private Collection<GroupRecord> records;
}
