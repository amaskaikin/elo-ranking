package com.tretton37.ranking.elo.web;

import com.tretton37.ranking.elo.dto.PageResponse;
import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.dto.search.PlayerSearchCriteria;
import com.tretton37.ranking.elo.errorhandling.ErrorResponse;
import com.tretton37.ranking.elo.service.player.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @Operation(summary = "List players")
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
                                           }) Pageable page,
                                           @RequestParam(required = false) UUID tournamentId) {
        return new PageResponse<>(playerService.getPlayers(page, tournamentId));
    }

    @Operation(summary = "Find players by specified criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Array of players"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    // ToDo: Update with `@ParameterObject` once it start working in OpenAPI lib
    public Collection<Player> findPlayers(@RequestParam(required = false) String name,
                                          @RequestParam(required = false) UUID tournamentId) {
        return playerService.find(new PlayerSearchCriteria(name, tournamentId));
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
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Player updatePlayer(@PathVariable UUID id,
                               @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                       content = @Content(examples = {
                                               @ExampleObject(
                                                       name = "Update player request",
                                                       description = "Delta update is supported: only fields specified in request are updated",
                                                       value = """
                                                               {
                                                                  "name": "Name Mod",
                                                                  "rating": 1501
                                                               }
                                                               """
                                               )
                                       }))
                               @RequestBody Player player) {
        return playerService.deltaUpdate(id, player);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created Player"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409", description = "Player already exists",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> createPlayer(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(
                            name = "Create player request",
                            description = "`UUID`, `rating`, `registeredWhen` and `gamesPlayed` " +
                                    "fields are populated automatically",
                            value = """
                                    {
                                      "name": "Name Surname",
                                      "email": "name.surname@mail.test",
                                      "tournamentRef": {
                                          "id": "c81c5e26-33c7-4eca-8c0f-9a11f9a24e05"
                                      }
                                    }
                                    """)
            }))
            @Valid @RequestBody Player player) {
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
    @PostMapping(value = "/create/bulk", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> bulkCreatePlayers(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(
                            name = "Bulk create player request",
                            description = "`UUID`, `rating`, `registeredWhen` and `gamesPlayed` " +
                                    "fields are populated automatically",
                            value = """
                                    [
                                        {
                                            "name": "NameA SurnameA",
                                            "email": "nameA.surnameA@mail.test",
                                            "tournamentRef": {
                                                "id": "c81c5e26-33c7-4eca-8c0f-9a11f9a24e05"
                                            }
                                        },
                                        {
                                            "name": "NameB SurnameB",
                                            "email": "nameB.surnameB@mail.test",
                                            "tournamentRef": {
                                                "id": "c81c5e26-33c7-4eca-8c0f-9a11f9a24e05"
                                            }
                                        }
                                    ]
                                    """)
            }))
            @Valid @RequestBody Collection<Player> players) {
        playerService.bulkCreate(players);

        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Player not found",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable UUID id) {
        playerService.delete(id);
        return ResponseEntity.ok().build();
    }
}
