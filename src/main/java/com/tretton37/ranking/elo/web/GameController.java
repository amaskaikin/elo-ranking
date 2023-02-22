package com.tretton37.ranking.elo.web;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.PageResponse;
import com.tretton37.ranking.elo.dto.SearchCriteria;
import com.tretton37.ranking.elo.service.GameService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Game> getGames(@PageableDefault(size = 30, sort = "playedWhen",
            direction = Sort.Direction.DESC) Pageable page) {
        log.debug("Request /list: {}", page);
        return gameService.getGames(page);
    }

    @GetMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<Game> findGames(@RequestParam SearchCriteria criteria,
                                        @PageableDefault(size = 30, sort = "playedWhen",
                                        direction = Sort.Direction.DESC) Pageable page) {
        log.debug("Request /find: criteria={}, page={}", criteria, page);
        return new PageResponse<>(gameService.findGames(criteria, page));
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Game createGame(@Valid @RequestBody Game game) {
        log.info("registerGame: {}", game);
        return gameService.registerGame(game);
    }
}

