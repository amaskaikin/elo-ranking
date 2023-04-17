package com.tretton37.ranking.elo.application.utils;

import com.tretton37.ranking.elo.adapter.persistence.GameGateway;
import com.tretton37.ranking.elo.domain.model.Game;
import com.tretton37.ranking.elo.domain.model.GameStatus;
import com.tretton37.ranking.elo.domain.model.PendingGameRef;
import com.tretton37.ranking.elo.domain.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class PlayerDetailsEnrichmentHelper {

    private final GameGateway gameGateway;

    @Autowired
    public PlayerDetailsEnrichmentHelper(GameGateway gameGateway) {
        this.gameGateway = gameGateway;
    }

    public void enrich(Player player) {
        enrichWithPendingGames(player);
    }

    private void enrichWithPendingGames(Player player) {
        Collection<Game> pendingGames = gameGateway
                .findByPlayerAndStatus(player.getId(), GameStatus.PENDING.name());

        var pendingGamesRefs = pendingGames.stream()
                .map(game -> PendingGameRef.builder()
                        .gameId(game.getId())
                        .initiator(game.getPlayerRefA())
                        .approver(game.getPlayerRefB())
                        .playedWhen(game.getPlayedWhen())
                        .build())
                .toList();
        player.setPendingGameRefs(pendingGamesRefs);
    }
}
