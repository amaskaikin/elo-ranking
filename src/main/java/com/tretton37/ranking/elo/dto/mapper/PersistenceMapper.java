package com.tretton37.ranking.elo.dto.mapper;


public interface PersistenceMapper<Dto, Entity> {

    Dto entityToDto(Entity entity);
    Entity dtoToEntity(Dto dto);
}
