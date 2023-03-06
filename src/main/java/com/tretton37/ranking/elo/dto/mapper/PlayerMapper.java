package com.tretton37.ranking.elo.dto.mapper;

import com.tretton37.ranking.elo.dto.Player;
import com.tretton37.ranking.elo.persistence.GameRepository;
import com.tretton37.ranking.elo.persistence.entity.PlayerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper implements PersistenceMapper<Player, PlayerEntity> {

    private final GameRepository gameRepository;
    private final TournamentMapper tournamentMapper;

    @Autowired
    public PlayerMapper(GameRepository gameRepository,
                        TournamentMapper tournamentMapper) {
        this.gameRepository = gameRepository;
        this.tournamentMapper = tournamentMapper;
    }

    @Override
    public Player entityToDto(PlayerEntity playerEntity) {
        return Player.builder()
                .id(playerEntity.getId())
                .name(playerEntity.getName())
                .email(playerEntity.getEmail())
                .profileImage(playerEntity.getProfileImage())
                .tournamentRef(tournamentMapper.entityToDto(playerEntity.getTournament()))
                .rating(playerEntity.getRating())
                .registeredWhen(playerEntity.getRegisteredWhen())
                .gamesPlayed(gameRepository.countByPlayer(playerEntity.getId()))
                .reachedHighRating(playerEntity.isReachedHighRating())
                .build();
    }

    @Override
    public PlayerEntity dtoToEntity(Player player) {
        return PlayerEntity.builder()
                .id(player.getId())
                .name(player.getName())
                .email(player.getEmail())
                .profileImage(player.getProfileImage())
                .tournament(tournamentMapper.dtoToEntity(player.getTournamentRef()))
                .rating(player.getRating())
                .registeredWhen(player.getRegisteredWhen())
                .reachedHighRating(player.isReachedHighRating())
                .build();
    }
}
