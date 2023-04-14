package com.tretton37.ranking.elo.domain.service.game;

import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.search.GameSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GameService {
    Page<Game> getAll(Pageable pageable);
    Page<Game> find(GameSearchCriteria gameSearchCriteria, Pageable pageable);
    Game findById(UUID id);
    Game register(Game game);
    void delete(UUID id);
}
