package com.tretton37.ranking.elo.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerScore {
    private PlayerRef playerRef;
    private Integer score;
    private Integer ratingAlteration;
}
