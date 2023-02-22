package com.tretton37.ranking.elo.service;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.dto.SearchCriteria;
import com.tretton37.ranking.elo.dto.mapper.PersistenceMapper;
import com.tretton37.ranking.elo.persistence.GameRepository;
import com.tretton37.ranking.elo.persistence.entity.GameEntity;
import com.tretton37.ranking.elo.persistence.entity.GameResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.jpa.domain.Specification.allOf;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@Slf4j
public class GameService {

    private final GameRepository gameRepository;
    private final PersistenceMapper<Game, GameEntity> mapper;
    private final PlayerService playerService;
    private final EloCalculatorService eloCalculatorService;

    @Autowired
    public GameService(GameRepository gameRepository,
                       PersistenceMapper<Game, GameEntity> mapper,
                       PlayerService playerService,
                       EloCalculatorService eloCalculatorService) {
        this.gameRepository = gameRepository;
        this.mapper = mapper;
        this.playerService = playerService;
        this.eloCalculatorService = eloCalculatorService;
    }

    public Page<Game> getGames(Pageable pageable) {
        return gameRepository.findAll(pageable)
                .map(mapper::entityToDto);
    }

    public Page<Game> findGames(SearchCriteria searchCriteria, Pageable pageable) {
        return gameRepository.findAll(buildFilterSpecification(Objects.requireNonNull(searchCriteria)), pageable)
                .map(mapper::entityToDto);
    }

    public Game registerGame(Game game) {
        Player playerA = getOrLoadPlayer(game.getPlayerA());
        Player playerB = getOrLoadPlayer(game.getPlayerB());

        eloCalculatorService.updateEloRatings(playerA, playerB, game.getResult());
        playerService.deltaUpdateBatch(List.of(playerA, playerB));

        game.setPlayerA(playerA);
        game.setPlayerB(playerB);
        game.setPlayedWhen(LocalDateTime.now());

        return mapper.entityToDto(
                gameRepository.save(mapper.dtoToEntity(game))
        );
    }

    private Player getOrLoadPlayer(Player player) {
        if (needFetchPlayerDetails(Objects.requireNonNull(player))) {
            return playerService.findById(player.getId());
        }

        return player;
    }

    private boolean needFetchPlayerDetails(Player player) {
       return Stream.of(player.getRating(), player.getGamesPlayed())
               .allMatch(Objects::isNull);
    }

    private Specification<GameEntity> buildFilterSpecification(final SearchCriteria searchCriteria) {
        return where(allOf(Objects.requireNonNullElse(searchCriteria.players(), Collections.<UUID>emptyList())
                        .stream()
                        .map(this::playerIs)
                        .collect(Collectors.toSet())
                )
                .and(resultIs(searchCriteria.result()))
        );
    }

    private Specification<GameEntity> playerIs(UUID id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.equal(root.get("playera_id"), id.toString()),
                criteriaBuilder.equal(root.get("playerb_id"), id.toString())
        );
    }

    private Specification<GameEntity> resultIs(GameResult result) {
        if (result == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("result"), result.name());
    }
}
