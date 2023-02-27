package com.tretton37.ranking.elo.web;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.PageResponse;
import com.tretton37.ranking.elo.dto.SearchCriteria;
import com.tretton37.ranking.elo.errorhandling.ErrorResponse;
import com.tretton37.ranking.elo.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @Operation(summary = "List games")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pageable content of Games"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PageableAsQueryParam
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<Game> getGames(@PageableDefault(size = 30, sort = "playedWhen",
            direction = Sort.Direction.DESC) Pageable page) {
        log.debug("Request /list: {}", page);
        return new PageResponse<>(gameService.getGames(page));
    }

    @Operation(summary = "Find games by specified criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pageable content of Games"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PageableAsQueryParam
    @PostMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<Game> findGames(@PageableDefault(size = 30, sort = "playedWhen",
            direction = Sort.Direction.DESC) @ParameterObject Pageable page,
                                        @Valid @RequestBody SearchCriteria criteria) {
        log.debug("Request /find: criteria={}, page={}", criteria, page);
        return new PageResponse<>(gameService.findGames(criteria, page));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requested Game",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Game.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Game not found",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Game getGameById(@PathVariable UUID id) {
        log.debug("Request /id: id={}", id);
        return gameService.findGameById(id);
    }

    @Operation(description = "Register game. Players ranks will be recalculated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created Game"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Game> createGame(@Valid @RequestBody Game game) {
        log.info("registerGame: {}", game);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gameService.registerGame(game));
    }
}

