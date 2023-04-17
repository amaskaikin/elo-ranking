package com.tretton37.ranking.elo.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PendingGameRef {
    private UUID gameId;
    private PlayerRef initiator;
    private PlayerRef approver;
    private LocalDateTime playedWhen;
}
