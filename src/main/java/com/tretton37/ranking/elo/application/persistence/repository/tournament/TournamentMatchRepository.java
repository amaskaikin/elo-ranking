package com.tretton37.ranking.elo.application.persistence.repository.tournament;

import com.tretton37.ranking.elo.application.persistence.entity.tournament.TournamentMatchEntity;
import com.tretton37.ranking.elo.domain.model.tournament.bracket.BracketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface TournamentMatchRepository extends JpaRepository<TournamentMatchEntity, UUID> {
    Collection<TournamentMatchEntity> findAllByTournamentIdAndBracketTypeIn(UUID tournamentId, BracketType... bracketTypes);
}
