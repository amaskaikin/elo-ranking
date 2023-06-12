package com.tretton37.ranking.elo.application.persistence.entity.tournament.group;

import com.tretton37.ranking.elo.application.persistence.entity.tournament.TournamentEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tournament_group")
public class GroupEntity {
    @Id
    private UUID id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private TournamentEntity tournament;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @OrderBy("points DESC")
    private Collection<GroupRecordEntity> records;
}
