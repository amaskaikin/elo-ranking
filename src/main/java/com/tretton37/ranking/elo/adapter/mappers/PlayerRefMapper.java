package com.tretton37.ranking.elo.adapter.mappers;

import com.tretton37.ranking.elo.domain.model.PlayerRef;
import com.tretton37.ranking.elo.application.persistence.entity.PlayerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlayerRefMapper {
    PlayerRef entityToDto(PlayerEntity playerEntity);
    PlayerEntity dtoToEntity(PlayerRef playerRef);
}
