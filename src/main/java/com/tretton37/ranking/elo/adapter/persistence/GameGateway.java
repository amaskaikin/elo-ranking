package com.tretton37.ranking.elo.adapter.persistence;

import com.tretton37.ranking.elo.adapter.mappers.GameMapper;
import com.tretton37.ranking.elo.application.persistence.repository.GameRepository;
import com.tretton37.ranking.elo.application.persistence.specification.GameSpecificationBuilder;
import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.search.GameSearchCriteria;
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
    private final GameMapper mapper;

    @Autowired
    public GameGateway(GameRepository gameRepository,
                       GameMapper mapper) {
        this.gameRepository = gameRepository;
        this.mapper = mapper;
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
        return mapper.entityToDto(gameRepository.save(mapper.dtoToEntity(game)));
    }

    public void delete(UUID id) {
        gameRepository.deleteById(id);
    }
}
