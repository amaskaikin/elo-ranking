package com.tretton37.ranking.elo.adapter.persistence;

import com.tretton37.ranking.elo.adapter.mappers.PlayerMapper;
import com.tretton37.ranking.elo.application.persistence.entity.PlayerEntity;
import com.tretton37.ranking.elo.application.persistence.repository.PlayerRepository;
import com.tretton37.ranking.elo.application.persistence.specification.PlayerSpecificationBuilder;
import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.search.PlayerFilteringCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PlayerGateway {
    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    @Autowired
    public PlayerGateway(PlayerRepository playerRepository,
                         PlayerMapper playerMapper) {
        this.playerRepository = playerRepository;
        this.playerMapper = playerMapper;
    }

    public Collection<Player> find(PlayerFilteringCriteria filteringCriteria) {
        return playerRepository.findAll(PlayerSpecificationBuilder
                        .forCriteria(filteringCriteria)
                        .build())
                .stream()
                .map(playerMapper::entityToDto)
                .toList();
    }

    public Collection<Player> find(Collection<UUID> ids) {
        return playerRepository.findAllById(ids)
                .stream()
                .map(playerMapper::entityToDto)
                .toList();
    }

    public Page<Player> find(PlayerFilteringCriteria filteringCriteria, Pageable pageable) {
        return playerRepository.findAll(PlayerSpecificationBuilder
                        .forCriteria(filteringCriteria)
                        .build(), pageable)
                .map(playerMapper::entityToDto);
    }

    public Optional<Player> findById(UUID uuid) {
        Optional<PlayerEntity> playerEntity = playerRepository.findById(uuid);
        return playerEntity.flatMap(e -> Optional.ofNullable(playerMapper.entityToDto(e)));
    }

    public Player findByEmail(String email) {
        PlayerEntity playerEntity = playerRepository.findByEmail(email);
        if (playerEntity == null) {
            return null;
        }
        return playerMapper.entityToDto(playerEntity);
    }

    public Player save(Player player) {
        return playerMapper.entityToDto(
                playerRepository.save(playerMapper.dtoToEntity(player))
        );
    }

    public void saveAll(Collection<Player> players) {
        playerRepository.saveAll(players.stream()
                .map(playerMapper::dtoToEntity)
                .collect(Collectors.toList())
        );
    }

    public void delete(UUID id) {
        playerRepository.deleteById(id);
    }
}
