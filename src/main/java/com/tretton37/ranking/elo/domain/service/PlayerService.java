package com.tretton37.ranking.elo.domain.service;

import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.search.PlayerListFilteringCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.UUID;

public interface PlayerService {
    Page<Player> list(PlayerListFilteringCriteria filteringCriteria, Pageable pageable);
    Collection<Player> find(String email, String name);
    Player findById(UUID uuid);
    Player create(Player player);
    void bulkCreate(Collection<Player> players);
    Player deltaUpdate(UUID id, Player player);
    void deltaUpdateBatch(Collection<Player> players);
    void delete(UUID id);
}
