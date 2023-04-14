package com.tretton37.ranking.elo.application.persistence.repository;

import com.tretton37.ranking.elo.application.persistence.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, UUID>, JpaSpecificationExecutor<PlayerEntity> {
    PlayerEntity findByEmail(String email);

    @Query(value = """
            select *
             from player p
            where unaccent(p.name) ilike unaccent(concat('%', :name, '%'))
           """, nativeQuery = true)
    Collection<PlayerEntity> findAllByNormalizedName(String name);
}
