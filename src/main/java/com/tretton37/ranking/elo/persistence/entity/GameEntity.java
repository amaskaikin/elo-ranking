package com.tretton37.ranking.elo.persistence.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "game")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playera_id", nullable = false)
    private PlayerEntity playerA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playerb_id", nullable = false)
    private PlayerEntity playerB;

    @Enumerated(EnumType.STRING)
    private GameResult result;

    private LocalDateTime playedWhen;

}
