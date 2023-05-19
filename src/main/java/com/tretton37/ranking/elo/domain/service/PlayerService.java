package com.tretton37.ranking.elo.domain.service;

import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.search.PlayerFilteringCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.UUID;

public interface PlayerService {
    Page<Player> list(PlayerFilteringCriteria filteringCriteria, Pageable pageable);
    Collection<Player> find(PlayerFilteringCriteria filteringCriteria);
    Player findById(UUID uuid);
    Player create(Player player);
    void bulkCreate(Collection<Player> players);
    Player deltaUpdate(UUID id, Player player);
    void deltaUpdateBatch(Collection<Player> players);
    void delete(UUID id);
}
