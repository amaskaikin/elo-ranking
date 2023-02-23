package com.tretton37.ranking.elo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.dto.PlayerRef;
import com.tretton37.ranking.elo.dto.mapper.PlayerMapper;
import com.tretton37.ranking.elo.errorhandling.EntityAlreadyExistsException;
import com.tretton37.ranking.elo.errorhandling.EntityNotFoundException;
import com.tretton37.ranking.elo.errorhandling.ErrorDetails;
import com.tretton37.ranking.elo.persistence.PlayerRepository;
import com.tretton37.ranking.elo.persistence.entity.PlayerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlayerService {
    @Value("${elo.ranking.initial-rank}")
    private Integer initialRank;

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    private final ObjectMapper objectMapper;

    @Autowired
    public PlayerService(PlayerRepository playerRepository,
                         PlayerMapper playerMapper,
                         ObjectMapper objectMapper) {
        this.playerRepository = playerRepository;
        this.playerMapper = playerMapper;
        this.objectMapper = objectMapper;
    }

    public Page<Player> getPlayers(Pageable pageable) {
        return playerRepository.findAll(pageable)
                .map(playerMapper::entityToDto);
    }

    public Collection<Player> findPlayersByNameLike(String name) {
        return playerRepository.findByNameContainingIgnoreCase(name).stream()
                .map(playerMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public Player findById(UUID uuid) {
        return playerMapper.entityToDto(getById(uuid));
    }

    public Player create(Player player) {
        if (playerRepository.findByName(player.getName()) != null) {
            throw new EntityAlreadyExistsException(ErrorDetails.ENTITY_ALREADY_EXISTS,
                    "Player with the same name already exists");
        }
        enrichPlayerBeforeSave(player);

        return playerMapper.entityToDto(
                playerRepository.save(playerMapper.dtoToEntity(player))
        );
    }

    public void bulkCreate(Collection<Player> players) {
        playerRepository.saveAll(players.stream()
                .map(p -> {
                    enrichPlayerBeforeSave(p);
                    return playerMapper.dtoToEntity(p);
                })
                .collect(Collectors.toList())
        );
    }

    public void deltaUpdateBatch(Collection<Player> players) {
        Collection<PlayerEntity> updatedPlayers = players.stream()
                .map(player -> mergeForUpdate(getById(player.getId()), playerMapper.dtoToEntity(player)))
                .toList();

        playerRepository.saveAll(updatedPlayers);
    }

    public Player deltaUpdate(UUID id, Player player) {
        return playerMapper.entityToDto(
                playerRepository.save(
                        mergeForUpdate(getById(id), playerMapper.dtoToEntity(player))
                )
        );
    }

    public PlayerRef convertDtoToReference(Player dto) {
        return PlayerRef.builder()
                .id(dto.getId())
                .name(dto.getName())
                .rating(dto.getRating())
                .build();
    }

    private PlayerEntity getById(UUID uuid) {
        return playerRepository.findById(uuid).orElseThrow(() -> new EntityNotFoundException(
                ErrorDetails.ENTITY_NOT_FOUND, "Player is not found by id: " + uuid));
    }

    private PlayerEntity mergeForUpdate(PlayerEntity existing, PlayerEntity updated) {
        try {
            return objectMapper.readerForUpdating(existing)
                    .readValue(objectMapper.convertValue(updated, JsonNode.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void enrichPlayerBeforeSave(Player player) {
        player.setRating(initialRank);
        player.setRegisteredWhen(LocalDateTime.now());
    }
}
