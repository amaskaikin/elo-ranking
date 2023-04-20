package com.tretton37.ranking.elo.adapter.persistence;

import com.tretton37.ranking.elo.adapter.mappers.PersistenceMapper;
import com.tretton37.ranking.elo.application.persistence.entity.AchievementEntity;
import com.tretton37.ranking.elo.application.persistence.repository.AchievementRepository;
import com.tretton37.ranking.elo.domain.model.Achievement;
import com.tretton37.ranking.elo.domain.model.AchievementDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AchievementGateway {

    private final AchievementRepository achievementRepository;
    private final PersistenceMapper<Achievement, AchievementEntity> mapper;

    @Autowired
    public AchievementGateway(AchievementRepository achievementRepository,
                              PersistenceMapper<Achievement, AchievementEntity> mapper) {
        this.achievementRepository = achievementRepository;
        this.mapper = mapper;
    }

    public Optional<Achievement> getById(UUID id) {
        return achievementRepository.findById(id).flatMap(e -> Optional.ofNullable(mapper.entityToDto(e)));
    }

    public Collection<Achievement> getAll() {
        return achievementRepository.findAll()
                .stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    public Optional<Achievement> findByType(AchievementDef type) {
        return achievementRepository.findByType(type)
                .flatMap(e -> Optional.ofNullable(mapper.entityToDto(e)));
    }

    public Achievement save(Achievement achievement) {
        return mapper.entityToDto(achievementRepository.save(mapper.dtoToEntity(achievement)));
    }

    public void delete(UUID id) {
        achievementRepository.deleteById(id);
    }
}
