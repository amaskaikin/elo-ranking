package com.tretton37.ranking.elo.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "game_result")
public class GameResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer playerAScore;
    private Integer playerBScore;

    @Column(name = "winner_id")
    private UUID winnerId;

    @Column(name = "playerA_rating_alt")
    private Integer playerARatingAlteration;
    @Column(name = "playerB_rating_alt")
    private Integer playerBRatingAlteration;
}
