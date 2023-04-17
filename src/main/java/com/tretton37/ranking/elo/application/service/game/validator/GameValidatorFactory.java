package com.tretton37.ranking.elo.application.service.game.validator;

import com.tretton37.ranking.elo.domain.service.game.GameLifecycleStage;
import com.tretton37.ranking.elo.domain.service.game.validator.GameValidator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class GameValidatorFactory {
    private static final Map<GameLifecycleStage, GameValidator> validatorCache = new HashMap<>();
    private final List<GameValidator> validators;

    @Autowired
    public GameValidatorFactory(List<GameValidator> validators) {
        this.validators = validators;
    }

    @PostConstruct
    public void initValidatorCache() {
        validators.forEach(v -> validatorCache.put(v.getStage(), v));
    }

    public GameValidator getValidator(GameLifecycleStage stage) {
        return Optional.ofNullable(validatorCache.get(stage))
                .orElseThrow(() -> new UnsupportedOperationException("Unknown lifecycle stage: " + stage));
    }
}

