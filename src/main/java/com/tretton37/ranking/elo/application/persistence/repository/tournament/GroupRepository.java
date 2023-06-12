package com.tretton37.ranking.elo.application.persistence.repository.tournament;

import com.tretton37.ranking.elo.application.persistence.entity.tournament.group.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<GroupEntity, UUID> {
    Collection<GroupEntity> findAllByTournamentId(UUID tournamentId);
}
