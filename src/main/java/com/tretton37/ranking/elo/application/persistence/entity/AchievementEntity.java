package com.tretton37.ranking.elo.application.persistence.entity;

import com.tretton37.ranking.elo.domain.model.AchievementDef;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "achievement")
public class AchievementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Enumerated(EnumType.STRING)
    private AchievementDef type;
    private String name;
    private String description;
    private String icon;

    public AchievementEntity(UUID id, AchievementDef type, String name,
                             String description, String icon) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "achievements")
    private Set<PlayerEntity> players;
}
