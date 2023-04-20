package com.tretton37.ranking.elo.application.persistence.repository;

import com.tretton37.ranking.elo.application.persistence.entity.AchievementEntity;
import com.tretton37.ranking.elo.domain.model.AchievementDef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AchievementRepository extends JpaRepository<AchievementEntity, UUID> {
    @Transactional(readOnly = true)
    Optional<AchievementEntity> findByType(AchievementDef type);
}
