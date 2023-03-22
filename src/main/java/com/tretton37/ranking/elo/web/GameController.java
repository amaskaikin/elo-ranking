package com.tretton37.ranking.elo.web;

import com.tretton37.ranking.elo.dto.Game;
import com.tretton37.ranking.elo.dto.PageResponse;
import com.tretton37.ranking.elo.dto.search.GameSearchCriteria;
import com.tretton37.ranking.elo.errorhandling.ErrorResponse;
import com.tretton37.ranking.elo.service.game.GameService;
import com.tretton37.ranking.elo.service.validator.RequestValidator;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final RequestValidator<Game> gameRequestValidator;

    @Autowired
    public GameController(GameService gameService,
                          RequestValidator<Game> gameRequestValidator) {
        this.gameService = gameService;
        this.gameRequestValidator = gameRequestValidator;
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
    @GetMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<Game> findGames(@PageableDefault(size = 30, sort = "playedWhen",
            direction = Sort.Direction.DESC) @ParameterObject Pageable page,
                                        @RequestParam(required = false) Collection<UUID> playerIds,
                                        @RequestParam(required = false) UUID winnerId,
                                        @RequestParam(required = false) UUID tournamentId) {
        GameSearchCriteria criteria = new GameSearchCriteria(playerIds, winnerId, tournamentId);
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
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Game> createGame(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(
                            name = "Create game request",
                            description = "To register the game only reference ids are required",
                            value = """
                                    {
                                        "playerRefA": {
                                            "id": "bd9e0e71-10b5-4e25-b280-0b89b14832fa"
                                        },
                                        "playerRefB": {
                                            "id": "3d259076-6f85-4368-9ef3-b9256d5d8205"
                                        },
                                        "tournamentRef": {
                                            "id": "c81c5e26-33c7-4eca-8c0f-9a11f9a24e05"
                                        },
                                        "gameResult": {
                                            "playerAScore": 8,
                                            "playerBScore": 11,
                                            "winnerId": "3d259076-6f85-4368-9ef3-b9256d5d8205"
                                        }
                                    }
                                    """)
            }))
            @Valid @RequestBody Game game) {
        log.info("registerGame: {}", game);
        gameRequestValidator.validate(game);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gameService.registerGame(game));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Game not found",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable UUID id) {
        gameService.deleteGame(id);
        return ResponseEntity.ok().build();
    }
}

