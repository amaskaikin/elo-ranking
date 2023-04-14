package com.tretton37.ranking.elo.application.service.game;

import com.tretton37.ranking.elo.adapter.persistence.GameGateway;
import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.exception.EntityNotFoundException;
import com.tretton37.ranking.elo.domain.model.exception.ErrorDetails;
import com.tretton37.ranking.elo.domain.model.search.GameSearchCriteria;
import com.tretton37.ranking.elo.domain.service.game.GameLifecycleManager;
import com.tretton37.ranking.elo.domain.service.game.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class GameServiceImpl implements GameService {

    private final GameGateway gameGateway;
    private final GameLifecycleManager gameLifecycleManager;

    @Autowired
    public GameServiceImpl(GameGateway gameGateway, GameLifecycleManager gameLifecycleManager) {
        this.gameGateway = gameGateway;
        this.gameLifecycleManager = gameLifecycleManager;
    }

    @Override
    public Page<Game> getAll(Pageable pageable) {
        return gameGateway.getAll(pageable);
    }

    @Override
    public Page<Game> find(GameSearchCriteria gameSearchCriteria, Pageable pageable) {
        return gameGateway.find(gameSearchCriteria, pageable);
    }

    @Override
    public Game findById(UUID id) {
        return gameGateway.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ErrorDetails.ENTITY_NOT_FOUND, "Game is not found by id: " + id));
    }

    @Override
    public Game register(Game game) {
        gameLifecycleManager.register(game);

        return gameGateway.save(game);
    }

    @Override
    public void delete(UUID id) {
        log.info("delete: Deleting game: {}", id);
        gameGateway.delete(id);
    }
}
