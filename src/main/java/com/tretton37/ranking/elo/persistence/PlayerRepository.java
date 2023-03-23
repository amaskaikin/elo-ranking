package com.tretton37.ranking.elo.persistence;

import com.tretton37.ranking.elo.persistence.entity.PlayerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, UUID>, JpaSpecificationExecutor<PlayerEntity> {
    PlayerEntity findByEmail(String email);
    Page<PlayerEntity> findAllByTournamentId(UUID tournamentId, Pageable pageable);
}
