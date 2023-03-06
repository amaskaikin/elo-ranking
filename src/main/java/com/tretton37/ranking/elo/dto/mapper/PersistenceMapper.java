package com.tretton37.ranking.elo.dto.mapper;


// ToDo: Consider if it worth to replace custom mappers with ModelMapper lib
public interface PersistenceMapper<Dto, Entity> {

    Dto entityToDto(Entity entity);
    Entity dtoToEntity(Dto dto);
}
