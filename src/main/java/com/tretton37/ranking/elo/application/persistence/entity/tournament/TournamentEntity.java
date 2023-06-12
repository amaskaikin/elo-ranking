package com.tretton37.ranking.elo.application.persistence.entity.tournament;

import com.tretton37.ranking.elo.application.persistence.entity.LocationEntity;
import com.tretton37.ranking.elo.domain.model.tournament.Stage;
import com.tretton37.ranking.elo.domain.model.tournament.TournamentType;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.MatchType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tournament")
public class TournamentEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private LocationEntity location;

    @Enumerated(EnumType.STRING)
    private TournamentType type;

    @Enumerated(EnumType.STRING)
    private Stage stage;

    @Enumerated(EnumType.STRING)
    private MatchType eliminationMatchType;

    @Enumerated(EnumType.STRING)
    private MatchType grandFinalMatchType;

    private Boolean ongoing;
    private Integer groupSize;
    private Integer winnersThreshold;
}