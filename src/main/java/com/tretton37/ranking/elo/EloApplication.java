package com.tretton37.ranking.elo;

import com.tretton37.ranking.elo.domain.model.search.GameSearchCriteria;
import com.tretton37.ranking.elo.domain.model.search.PlayerFilteringCriteria;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RegisterReflectionForBinding({PlayerFilteringCriteria.class, GameSearchCriteria.class})
public class EloApplication {
	public static void main(String[] args) {
		SpringApplication.run(EloApplication.class, args);
	}
}
