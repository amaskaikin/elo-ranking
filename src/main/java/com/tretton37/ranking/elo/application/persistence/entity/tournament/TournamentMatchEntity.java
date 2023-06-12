package com.tretton37.ranking.elo.application.persistence.entity.tournament;

import com.tretton37.ranking.elo.application.persistence.entity.GameEntity;
import com.tretton37.ranking.elo.application.persistence.entity.PlayerEntity;
import com.tretton37.ranking.elo.application.persistence.entity.tournament.group.GroupEntity;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.BracketType;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.MatchStatus;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.MatchType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tournament_match")
public class TournamentMatchEntity {
    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playera_id")
    private PlayerEntity playerA;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playerb_id")
    private PlayerEntity playerB;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prev_matcha_id", referencedColumnName = "id")
    private TournamentMatchEntity previousMatchA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prev_matchb_id", referencedColumnName = "id")
    private TournamentMatchEntity previousMatchB;

    @OneToMany(mappedBy = "tournamentMatch", cascade = CascadeType.ALL)
    private Collection<GameEntity> playedGames;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private TournamentEntity tournament;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private PlayerEntity winner;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @Enumerated(EnumType.STRING)
    private BracketType bracketType;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @Enumerated(EnumType.STRING)
    private MatchType matchType;

    private Integer round;
    private int playerAScore;
    private int playerBScore;
}
