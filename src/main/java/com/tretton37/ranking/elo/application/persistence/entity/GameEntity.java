package com.tretton37.ranking.elo.application.persistence.entity;

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
public class GameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "playera_id", nullable = false)
    private PlayerEntity playerA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playerb_id", nullable = false)
    private PlayerEntity playerB;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private TournamentEntity tournament;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "result_id", nullable = false)
    private GameResultEntity gameResult;

    private LocalDateTime playedWhen;

}
