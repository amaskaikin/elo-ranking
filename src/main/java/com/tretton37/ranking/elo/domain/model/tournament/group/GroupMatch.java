package com.tretton37.ranking.elo.domain.model.tournament.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tretton37.ranking.elo.domain.model.tournament.TournamentMatch;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GroupMatch extends TournamentMatch {
    private UUID groupId;
}
