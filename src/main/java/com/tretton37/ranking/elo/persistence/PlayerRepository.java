package com.tretton37.ranking.elo.persistence;

import com.tretton37.ranking.elo.persistence.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, UUID> {
    @Transactional(readOnly = true)
    Collection<PlayerEntity> findByNameContainingIgnoreCase(String name);
    @Transactional(readOnly = true)
    PlayerEntity findByName(String name);
}
