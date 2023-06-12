package com.tretton37.ranking.elo.adapter.persistence.tournament;

import com.tretton37.ranking.elo.adapter.mappers.tournament.GroupMapper;
import com.tretton37.ranking.elo.application.persistence.repository.tournament.GroupRecordRepository;
import com.tretton37.ranking.elo.application.persistence.repository.tournament.GroupRepository;
import com.tretton37.ranking.elo.domain.model.tournament.group.Group;
import com.tretton37.ranking.elo.domain.model.tournament.group.GroupRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class GroupGateway {
    private final GroupRepository repository;
    private final GroupRecordRepository recordRepository;
    private final GroupMapper mapper;

    @Autowired
    public GroupGateway(GroupRepository repository,
                        GroupRecordRepository recordRepository,
                        GroupMapper mapper) {
        this.repository = repository;
        this.recordRepository = recordRepository;
        this.mapper = mapper;
    }

    public Optional<Group> getById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDto);
    }

    public Collection<Group> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public Collection<Group> findAllByTournamentId(UUID tournamentId) {
        return repository.findAllByTournamentId(tournamentId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public Group save(Group group) {
        return mapper.toDto(repository.save(mapper.toEntity(group)));
    }

    public void saveAll(Collection<Group> groups) {
        repository.saveAll(groups.stream()
                .map(mapper::toEntity)
                .toList());
    }

    // ToDo: Maybe move this method somewhere outside
    public void saveAllRecords(GroupRecord... records) {
        recordRepository.saveAll(Stream.of(records)
                .map(mapper::toRecordEntity)
                .toList());
    }
}
