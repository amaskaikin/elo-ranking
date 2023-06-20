package com.tretton37.ranking.elo;

import com.tretton37.ranking.elo.domain.model.search.GameSearchCriteria;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RegisterReflectionForBinding({GameSearchCriteria.class, ImmutablePair.class})
public class EloApplication {
	public static void main(String[] args) {
		SpringApplication.run(EloApplication.class, args);
	}
}
