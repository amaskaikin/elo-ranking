package com.tretton37.ranking.elo.adapter.web.tournament;

import com.tretton37.ranking.elo.adapter.web.model.ErrorResponse;
import com.tretton37.ranking.elo.domain.model.tournament.Tournament;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.Bracket;
import com.tretton37.ranking.elo.domain.model.tournament.group.Group;
import com.tretton37.ranking.elo.domain.model.tournament.group.GroupMatch;
import com.tretton37.ranking.elo.domain.service.tournament.TournamentSeedingService;
import com.tretton37.ranking.elo.domain.service.tournament.TournamentService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.UUID;

@Tag(name = "tournament", description = "Tournament management API")
@RestController
@RequestMapping("/tournament")
public class TournamentController {
    private final TournamentService tournamentService;
    private final TournamentSeedingService seedingService;

    @Autowired
    public TournamentController(TournamentService tournamentService,
                                TournamentSeedingService seedingService) {
        this.tournamentService = tournamentService;
        this.seedingService = seedingService;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created Tournament"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Tournament> create(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(
                            name = "Create Tournament request",
                            description = "ID is generated automatically",
                            value = """
                                    {
                                        "name": "Office Ping Pong Tournament",
                                        "location": {
                                            "id": "73fec77a-736b-41a7-997e-3ac0e367bb8b"
                                        },
                                        "type": "GROUP_DOUBLE",
                                        "winnersThreshold": 2,
                                        "groupSize": 4,
                                        "eliminationMatchType": "BO_3",
                                        "grandFinalMatchType": "BO_5",
                                        "ongoing": true
                                    }
                                    """)
            })) @Valid @RequestBody Tournament Tournament) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tournamentService.persist(Tournament));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all Tournaments"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Tournament> getAll() {
        return tournamentService.getAll();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requested Tournament"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Tournament not found",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Tournament get(@PathVariable UUID id) {
        return tournamentService.getById(id);
    }

    @GetMapping(value = "/{id}/elimination", produces = MediaType.APPLICATION_JSON_VALUE)
    public Bracket getEliminationBracket(@PathVariable UUID id) {
        return tournamentService.getEliminationBracket(id);
    }

    @PostMapping(value = "/{id}/elimination/seed", produces = MediaType.APPLICATION_JSON_VALUE)
    public Bracket seedEliminationBracket(@PathVariable UUID id) {
        return seedingService.seedElimination(tournamentService.getById(id));
    }

    @PostMapping(value = "/{id}/group/seed", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Group> seedGroups(@PathVariable UUID id) {
        return seedingService.seedGroups(tournamentService.getById(id));
    }

    @GetMapping(value = "/{id}/group", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Group> getGroups(@PathVariable UUID id) {
        return tournamentService.getGroups(id);
    }

    @GetMapping(value = "/{id}/group/schedule", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<GroupMatch> getGroupMatches(@PathVariable UUID id) {
        return tournamentService.getGroupMatches(id);
    }
}
