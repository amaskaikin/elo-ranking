package com.tretton37.ranking.elo.application.persistence.entity;

import com.tretton37.ranking.elo.application.persistence.entity.tournament.TournamentMatchEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "game")
public class GameEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "playera_id", nullable = false)
    private PlayerEntity playerA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playerb_id", nullable = false)
    private PlayerEntity playerB;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity location;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "result_id", nullable = false)
    private GameResultEntity gameResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_match_id")
    private TournamentMatchEntity tournamentMatch;

    private LocalDateTime playedWhen;
}
