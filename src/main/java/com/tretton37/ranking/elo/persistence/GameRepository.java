package com.tretton37.ranking.elo.persistence;

import com.tretton37.ranking.elo.persistence.entity.GameEntity;
import com.tretton37.ranking.elo.persistence.entity.PlayerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GameRepository extends CrudRepository<GameEntity, UUID>, PagingAndSortingRepository<GameEntity, UUID>,
        JpaSpecificationExecutor<GameEntity> {

    Page<GameEntity> findAll(Pageable pageable);

    Page<GameEntity> findAll(Specification<GameEntity> specification, Pageable pageable);

    @Query(value = "select count(1) from game g where (g.playera_id = :playerId " +
            "OR g.playerb_id = :playerId)", nativeQuery = true)
    Integer countByPlayer(@Param("playerId") UUID playerId);
}

