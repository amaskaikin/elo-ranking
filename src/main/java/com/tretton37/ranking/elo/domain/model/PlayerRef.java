package com.tretton37.ranking.elo.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerRef {
    @NotNull
    private UUID id;
    private String name;
    private Integer rating;
}
