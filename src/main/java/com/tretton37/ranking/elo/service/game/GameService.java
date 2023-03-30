package com.tretton37.ranking.elo.service.game;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.search.GameSearchCriteria;
import com.tretton37.ranking.elo.dto.mapper.PersistenceMapper;
import com.tretton37.ranking.elo.errorhandling.EntityNotFoundException;
import com.tretton37.ranking.elo.errorhandling.ErrorDetails;
import com.tretton37.ranking.elo.persistence.GameRepository;
import com.tretton37.ranking.elo.persistence.entity.GameEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class GameService {

    private final GameRepository gameRepository;
    private final PersistenceMapper<Game, GameEntity> mapper;
    private final GameLifecycleManager lifecycleManager;

    @Autowired
    public GameService(GameRepository gameRepository,
                       PersistenceMapper<Game, GameEntity> mapper,
                       GameLifecycleManager lifecycleManager) {
        this.gameRepository = gameRepository;
        this.mapper = mapper;
        this.lifecycleManager = lifecycleManager;
    }

    public Page<Game> getGames(Pageable pageable) {
        return gameRepository.findAll(pageable)
                .map(mapper::entityToDto);
    }

    public Page<Game> findGames(GameSearchCriteria gameSearchCriteria, Pageable pageable) {
        return gameRepository.findAll(GameSpecificationBuilder
                        .forCriteria(Objects.requireNonNull(gameSearchCriteria))
                        .build(), pageable)
                .map(mapper::entityToDto);
    }

    public Game findGameById(UUID id) {
        return mapper.entityToDto(gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        ErrorDetails.ENTITY_NOT_FOUND, "Game is not found by id: " + id)));
    }

    public Game registerGame(Game game) {
        lifecycleManager.register(game);

        return mapper.entityToDto(gameRepository.save(mapper.dtoToEntity(game)));
    }

    public void deleteGame(UUID id) {
        log.info("deleteGame: Deleting game: {}", id);
        gameRepository.deleteById(id);
    }
}
