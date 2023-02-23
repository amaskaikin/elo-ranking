package com.tretton37.ranking.elo.persistence;

import com.tretton37.ranking.elo.persistence.entity.TournamentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface TournamentRepository extends JpaRepository<TournamentEntity, UUID> {
    @Transactional(readOnly = true)
    TournamentEntity findByName(String name);
}
