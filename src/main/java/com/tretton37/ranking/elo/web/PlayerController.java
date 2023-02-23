package com.tretton37.ranking.elo.web;

import com.tretton37.ranking.elo.dto.PageResponse;
import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.errorhandling.ErrorResponse;
import com.tretton37.ranking.elo.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping(value = "/player")
@Tag(name = "player", description = "Player management API")
public class PlayerController {
    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Operation(summary = "List playerIds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pageable content of Players"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PageableAsQueryParam
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<Player> getPlayers(@PageableDefault(size = 30)
                                           @SortDefault.SortDefaults({
                                                   @SortDefault(sort = "rating", direction = Sort.Direction.DESC),
                                                   @SortDefault(sort = "name", direction = Sort.Direction.ASC),
                                           })
                                           Pageable page) {
        return new PageResponse<>(playerService.getPlayers(page));
    }

    @Operation(summary = "Find playerIds by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Array of playerIds"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Player> getPlayersByName(@RequestParam String name) {
        return playerService.findPlayersByNameLike(name);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requested Player"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Player not found",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Player getPlayerById(@PathVariable UUID id) {
        return playerService.findById(id);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated Player"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Player not found",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Player updatePlayer(@PathVariable UUID id, @RequestBody Player player) {
        return playerService.deltaUpdate(id, player);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created Player"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> createPlayer(@Valid @RequestBody Player player) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playerService.create(player));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Players are successfully created"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping(value = "/create/bulk", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> bulkCreatePlayers(@Valid @RequestBody Collection<Player> players) {
        playerService.bulkCreate(players);

        return ResponseEntity.ok().build();
    }
}
