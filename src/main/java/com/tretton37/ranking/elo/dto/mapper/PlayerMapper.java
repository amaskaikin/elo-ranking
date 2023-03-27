package com.tretton37.ranking.elo.dto.mapper;

import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.dto.Tournament;
import com.tretton37.ranking.elo.persistence.entity.PlayerEntity;
import com.tretton37.ranking.elo.persistence.entity.TournamentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
                .build();
    }
}
