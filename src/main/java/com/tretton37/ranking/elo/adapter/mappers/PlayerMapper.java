package com.tretton37.ranking.elo.adapter.mappers;

import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.Tournament;
import com.tretton37.ranking.elo.application.persistence.entity.PlayerEntity;
import com.tretton37.ranking.elo.application.persistence.entity.TournamentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Component
public class PlayerMapper implements PersistenceMapper<Player, PlayerEntity> {

    private final PersistenceMapper<Tournament, TournamentEntity> tournamentMapper;
    private final JsonNullableMapper jsonNullableMapper;

    @Autowired
    public PlayerMapper(PersistenceMapper<Tournament, TournamentEntity> tournamentMapper,
                        JsonNullableMapper jsonNullableMapper) {
        this.tournamentMapper = tournamentMapper;
        this.jsonNullableMapper = jsonNullableMapper;
    }

    @Override
    public Player entityToDto(PlayerEntity playerEntity) {
        return Player.builder()
                .id(playerEntity.getId())
                .name(playerEntity.getName())
                .email(playerEntity.getEmail())
                .profileImage(jsonNullableMapper.wrap(playerEntity.getProfileImage()))
                .tournamentRef(tournamentMapper.entityToDto(playerEntity.getTournament()))
                .rating(playerEntity.getRating())
                .registeredWhen(playerEntity.getRegisteredWhen())
                .gamesPlayed(playerEntity.getGamesPlayed())
                .gamesWon(playerEntity.getGamesWon())
                .winRate(calculateWinRate(playerEntity))
                .reachedHighRating(playerEntity.isReachedHighRating())
                .build();
    }

    @Override
    public PlayerEntity dtoToEntity(Player player) {
        return PlayerEntity.builder()
                .id(player.getId())
                .name(player.getName())
                .email(player.getEmail())
                .profileImage(jsonNullableMapper.unwrap(player.getProfileImage()))
                .tournament(tournamentMapper.dtoToEntity(player.getTournamentRef()))
                .rating(player.getRating())
                .registeredWhen(player.getRegisteredWhen())
                .reachedHighRating(player.isReachedHighRating())
                .gamesPlayed(Optional.ofNullable(player.getGamesPlayed()).orElse(0))
                .gamesWon(Optional.ofNullable(player.getGamesWon()).orElse(0))
                .build();
    }

    private double calculateWinRate(PlayerEntity entity) {
        if (entity.getGamesPlayed() == 0) {
            return 0;
        }
        return BigDecimal.valueOf((double) entity.getGamesWon() / entity.getGamesPlayed())
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
