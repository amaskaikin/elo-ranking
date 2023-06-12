package com.tretton37.ranking.elo.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tretton37.ranking.elo.adapter.persistence.PlayerGateway;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.exception.EntityAlreadyExistsException;
import com.tretton37.ranking.elo.domain.model.exception.EntityNotFoundException;
import com.tretton37.ranking.elo.domain.model.exception.ErrorDetails;
import com.tretton37.ranking.elo.domain.model.search.PlayerFilteringCriteria;
import com.tretton37.ranking.elo.domain.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Service
@Slf4j
public class PlayerServiceImpl implements PlayerService {
    @Value("${elo.ranking.initial-rank}")
    private Integer initialRank;

    private final PlayerGateway playerGateway;
    private final ObjectMapper objectMapper;

    @Autowired
    public PlayerServiceImpl(PlayerGateway playerGateway, ObjectMapper objectMapper) {
        this.playerGateway = playerGateway;
        this.objectMapper = objectMapper;
    }

    @Override
    public Player findById(UUID uuid) {
        return playerGateway.findById(uuid).orElseThrow(() -> new EntityNotFoundException(
                ErrorDetails.ENTITY_NOT_FOUND, "Player is not found by id: " + uuid));
    }

    @Override
    public Page<Player> list(PlayerFilteringCriteria filteringCriteria, Pageable pageable) {
        return playerGateway.find(filteringCriteria, pageable);
    }

    @Override
    public Collection<Player> find(PlayerFilteringCriteria filteringCriteria) {
        return playerGateway.find(filteringCriteria);
    }

    @Override
    public Collection<Player> findAllByIds(Collection<UUID> ids) {
        return playerGateway.find(ids);
    }

    @Override
    public Player create(Player player) {
        if (playerGateway.findByEmail(player.getEmail()) != null) {
            throw new EntityAlreadyExistsException(ErrorDetails.ENTITY_ALREADY_EXISTS,
                    "Player with the same Email address already exists");
        }
        populateInitialParameters(player);

        return playerGateway.save(player);
    }

    @Override
    public void bulkCreate(Collection<Player> players) {
        players.forEach(this::populateInitialParameters);
        playerGateway.saveAll(players);
    }

    @Override
    public Player deltaUpdate(UUID id, Player player) {
        Player updated = mergeForUpdate(findById(id), player);
        log.debug("deltaUpdate: Merged delta update entity: id={}, entity={}", id, updated);

        return playerGateway.save(updated);
    }

    @Override
    public void deltaUpdateBatch(Collection<Player> players) {
        playerGateway.saveAll(players.stream()
                .map(player -> mergeForUpdate(findById(player.getId()), player))
                .toList());
    }

    @Override
    public void delete(UUID id) {
        playerGateway.delete(id);
    }

    private void populateInitialParameters(Player player) {
        player.setRating(initialRank);
        player.setRegisteredWhen(LocalDateTime.now());
    }

    private Player mergeForUpdate(Player existing, Player updated) {
        try {
            return objectMapper.readerForUpdating(existing)
                    .readValue(objectMapper.convertValue(updated, JsonNode.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
