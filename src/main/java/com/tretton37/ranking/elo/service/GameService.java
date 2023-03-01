package com.tretton37.ranking.elo.service;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.dto.SearchCriteria;
import com.tretton37.ranking.elo.dto.mapper.PersistenceMapper;
import com.tretton37.ranking.elo.errorhandling.EntityNotFoundException;
import com.tretton37.ranking.elo.errorhandling.ErrorDetails;
import com.tretton37.ranking.elo.persistence.GameRepository;
import com.tretton37.ranking.elo.persistence.entity.GameEntity;
import com.tretton37.ranking.elo.service.calculator.EloCalculatorService;
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

    public Game findGameById(UUID id) {
        return mapper.entityToDto(gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        ErrorDetails.ENTITY_NOT_FOUND, "Game is not found by id: " + id)));
    }

    public Game registerGame(Game game) {
        Player playerA = playerService.findById(game.getPlayerRefA().getId());
        Player playerB = playerService.findById(game.getPlayerRefB().getId());
        calculateWinner(game);

        eloCalculatorService.updateEloRatings(playerA, playerB, game);

        playerService.deltaUpdateBatch(List.of(playerA, playerB));
        game.setPlayedWhen(LocalDateTime.now());

        Game created = mapper.entityToDto(gameRepository.save(mapper.dtoToEntity(game)));
        created.setPlayerRefA(playerService.convertDtoToReference(playerA));
        created.setPlayerRefB(playerService.convertDtoToReference(playerB));

        return created;
    }

    private void calculateWinner(Game game) {
        Game.GameResult result = game.getGameResult();
        if (result.getWinnerId() != null || result.getPlayerAScore().equals(result.getPlayerBScore())) {
            log.trace("calculateWinner: Winner calculation is not required for Result: {}", result);
            return;
        }
        if (result.getPlayerAScore() > result.getPlayerBScore()) {
            log.trace("calculateWinner: Winner is PlayerA");
            result.setWinnerId(game.getPlayerRefA().getId());
        }
        if (result.getPlayerBScore() > result.getPlayerAScore()) {
            log.trace("calculateWinner: Winner is PlayerB");
            result.setWinnerId(game.getPlayerRefB().getId());
        }
    }

    private Specification<GameEntity> buildFilterSpecification(final SearchCriteria searchCriteria) {
        return where(allOf(Objects.requireNonNullElse(searchCriteria.playerIds(), Collections.<UUID>emptyList())
                        .stream()
                        .map(this::playerIs)
                        .collect(Collectors.toSet())
                )
                .and(winnerIs(searchCriteria.winnerId()))
                .and(tournamentIs(searchCriteria.tournamentId()))
        );
    }

    private Specification<GameEntity> playerIs(UUID id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.equal(root.get("playerA").get("id"), id),
                criteriaBuilder.equal(root.get("playerB").get("id"), id)
        );
    }

    private Specification<GameEntity> winnerIs(UUID winnerId) {
        if (winnerId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.join("gameResult")
                    .get("winnerId"), winnerId);
    }

    private Specification<GameEntity> tournamentIs(UUID tournamentId) {
        if (tournamentId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("tournament").get("id"), tournamentId);
    }
}
