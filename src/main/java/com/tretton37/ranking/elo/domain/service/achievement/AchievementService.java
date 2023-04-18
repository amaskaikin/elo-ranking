package com.tretton37.ranking.elo.domain.service.achievement;

import com.tretton37.ranking.elo.domain.model.Achievement;
import com.tretton37.ranking.elo.domain.model.AchievementDef;

import java.util.Collection;
import java.util.UUID;

public interface AchievementService {
    Achievement getById(UUID id);
    Achievement getByType(AchievementDef type);
    Collection<Achievement> getAll();
    Achievement create(Achievement achievement);
    void delete(UUID id);
}
