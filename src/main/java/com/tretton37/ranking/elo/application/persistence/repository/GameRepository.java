package com.tretton37.ranking.elo.application.persistence.repository;

import com.tretton37.ranking.elo.application.persistence.entity.GameEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, UUID>, PagingAndSortingRepository<GameEntity, UUID>,
        JpaSpecificationExecutor<GameEntity> {

    @NonNull
    @Override
    Page<GameEntity> findAll(@NonNull Pageable pageable);

    @NonNull
    @Override
    Page<GameEntity> findAll(Specification<GameEntity> specification, @NonNull Pageable pageable);

    @Query(value = """
            select *
             from game g
             where (g.playera_id = :playerId
                    or g.playerb_id = :playerId)
               and g.status = :status
           """, nativeQuery = true)
    Collection<GameEntity> findAllGamesByPlayerIdAndStatus(@Param("playerId") UUID playerId,
                                                           @Param("status") String status);
}

