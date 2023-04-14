package com.tretton37.ranking.elo.adapter.persistence;

import com.tretton37.ranking.elo.application.persistence.specification.GameSpecificationBuilder;
import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.search.GameSearchCriteria;
import com.tretton37.ranking.elo.adapter.mappers.PersistenceMapper;
import com.tretton37.ranking.elo.application.persistence.repository.GameRepository;
import com.tretton37.ranking.elo.application.persistence.entity.GameEntity;
import com.tretton37.ranking.elo.application.service.game.GameLifecycleManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class GameGateway {

    private final GameRepository gameRepository;
    private final PersistenceMapper<Game, GameEntity> mapper;
    private final GameLifecycleManagerImpl lifecycleManager;

    @Autowired
    public GameGateway(GameRepository gameRepository,
                       PersistenceMapper<Game, GameEntity> mapper,
                       GameLifecycleManagerImpl lifecycleManager) {
        this.gameRepository = gameRepository;
        this.mapper = mapper;
        this.lifecycleManager = lifecycleManager;
    }

    public Page<Game> getAll(Pageable pageable) {
        return gameRepository.findAll(pageable)
                .map(mapper::entityToDto);
    }

    public Page<Game> find(GameSearchCriteria gameSearchCriteria, Pageable pageable) {
        return gameRepository.findAll(GameSpecificationBuilder
                        .forCriteria(Objects.requireNonNull(gameSearchCriteria))
                        .build(), pageable)
                .map(mapper::entityToDto);
    }

    public Optional<Game> findById(UUID id) {
        return gameRepository.findById(id).flatMap(e -> Optional.ofNullable(mapper.entityToDto(e)));
    }

    public Game save(Game game) {
        lifecycleManager.register(game);

        return mapper.entityToDto(gameRepository.save(mapper.dtoToEntity(game)));
    }

    public void delete(UUID id) {
        gameRepository.deleteById(id);
    }
}
