package com.tretton37.ranking.elo.adapter.mappers;

import com.tretton37.ranking.elo.domain.model.Location;
import com.tretton37.ranking.elo.application.persistence.entity.LocationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    Location entityToDto(LocationEntity locationEntity);
    LocationEntity dtoToEntity(Location location);
}
