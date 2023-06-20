package com.tretton37.ranking.elo.application.service.achievement;

import com.tretton37.ranking.elo.adapter.persistence.AchievementGateway;
import com.tretton37.ranking.elo.domain.model.Achievement;
import com.tretton37.ranking.elo.domain.model.AchievementDef;
import com.tretton37.ranking.elo.domain.model.exception.EntityNotFoundException;
import com.tretton37.ranking.elo.domain.model.exception.ErrorDetails;
import com.tretton37.ranking.elo.domain.service.achievement.AchievementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
@Slf4j
public class AchievementServiceImpl implements AchievementService {
    private final AchievementGateway achievementGateway;

    @Autowired
    public AchievementServiceImpl(AchievementGateway achievementGateway) {
        this.achievementGateway = achievementGateway;
    }

    @Override
    public Achievement getById(UUID id) {
        return achievementGateway.getById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorDetails.ENTITY_NOT_FOUND,
                        "Achievement not found by Id: " + id));
    }

    @Override
    public Achievement getByType(AchievementDef type) {
        return achievementGateway.findByType(type)
                .orElseThrow(() -> new EntityNotFoundException(ErrorDetails.ENTITY_NOT_FOUND,
                        "Achievement not found by type: " + type));
    }

    @Override
    public Collection<Achievement> getAll() {
        return achievementGateway.getAll();
    }

    @Override
    public Achievement create(Achievement achievement) {
        return achievementGateway.save(achievement);
    }

    @Override
    public void delete(UUID id) {
        log.info("delete: Deleting achievement: {}", id);
        achievementGateway.delete(id);
    }
}
