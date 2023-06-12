package com.tretton37.ranking.elo.application.persistence.repository.tournament;

import com.tretton37.ranking.elo.application.persistence.entity.tournament.group.GroupRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupRecordRepository extends JpaRepository<GroupRecordEntity, UUID> {
}
