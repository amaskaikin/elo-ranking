package com.tretton37.ranking.elo.domain.service.tournament.group;

import com.tretton37.ranking.elo.domain.model.Player;
import com.tretton37.ranking.elo.domain.model.PlayerRef;
import com.tretton37.ranking.elo.domain.model.tournament.Tournament;
import com.tretton37.ranking.elo.domain.model.tournament.group.Group;
import com.tretton37.ranking.elo.domain.model.tournament.group.GroupRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GroupGenerator {
    private final Collection<Player> players;
    private final Tournament tournament;

    public GroupGenerator(Collection<Player> players, Tournament tournament) {
        this.players = players;
        this.tournament = tournament;
    }

    public Collection<Group> createGroups() {
        List<Player> registeredPlayers = new ArrayList<>(players);
        Collections.shuffle(registeredPlayers);
        int groupSize = tournament.getGroupSize();

        int numGroups = (int) Math.ceil((double) registeredPlayers.size() / groupSize);
        Collection<Group> groups = new ArrayList<>();

        for (int i = 0; i < numGroups; i++) {
            final Group group = new Group();
            group.setId(UUID.randomUUID());
            group.setName("Group " + (i + 1));
            group.setTournamentId(tournament.getId());

            int startIndex = i * groupSize;
            int endIndex = Math.min((i + 1) * groupSize, registeredPlayers.size());

            Collection<GroupRecord> records = IntStream.range(startIndex, endIndex)
                    .mapToObj(j -> {
                        var player = registeredPlayers.get(j);
                        PlayerRef playerRef = PlayerRef.builder()
                                .id(player.getId())
                                .name(player.getName())
                                .build();

                        GroupRecord groupRecord = new GroupRecord();
                        groupRecord.setId(UUID.randomUUID());
                        groupRecord.setPlayerRef(playerRef);
                        groupRecord.setGroupId(group.getId());

                        return groupRecord;
                    })
                    .collect(Collectors.toList());

            group.setRecords(records);
            groups.add(group);
        }

        return groups;
    }
}
