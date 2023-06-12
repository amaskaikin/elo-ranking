package com.tretton37.ranking.elo.application.persistence.repository.tournament;

import com.tretton37.ranking.elo.application.persistence.entity.tournament.TournamentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TournamentRepository extends JpaRepository<TournamentEntity, UUID>  {
}
