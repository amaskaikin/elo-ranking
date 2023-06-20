package com.tretton37.ranking.elo.adapter.mappers.tournament;

import com.tretton37.ranking.elo.adapter.mappers.PlayerRefMapper;
import com.tretton37.ranking.elo.application.persistence.entity.tournament.group.GroupEntity;
import com.tretton37.ranking.elo.application.persistence.entity.tournament.group.GroupRecordEntity;
import com.tretton37.ranking.elo.domain.model.tournament.group.Group;
import com.tretton37.ranking.elo.domain.model.tournament.group.GroupRecord;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PlayerRefMapper.class, RelationEntitiesQualifier.class})
public interface GroupMapper {
    @Mapping(target = "tournamentId", source = "tournament.id")
    Group toDto(GroupEntity groupEntity);
    @Mapping(source = "tournamentId", target = "tournament", qualifiedByName = "tournamentId")
    GroupEntity toEntity(Group group);

    @Mapping(target = "playerRef", source = "player")
    @Mapping(target = "groupId", source = "group.id")
    GroupRecord toRecordDto(GroupRecordEntity recordEntity);

    @Mapping(source = "groupId", target = "group", qualifiedByName = "groupId")
    @Mapping(target = "draw", ignore = true)
    @InheritInverseConfiguration
    GroupRecordEntity toRecordEntity(GroupRecord groupRecord);
}
