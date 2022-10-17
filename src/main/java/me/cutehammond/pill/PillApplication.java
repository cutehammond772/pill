package me.cutehammond.pill;

import me.cutehammond.pill.domain.pill.domain.dao.nosql.PillElementRepository;
import me.cutehammond.pill.domain.pill.domain.dao.sql.PillRepository;
import me.cutehammond.pill.domain.point.domain.dao.sql.PillPointRepository;
import me.cutehammond.pill.global.config.properties.AppProperties;
import me.cutehammond.pill.global.config.properties.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableConfigurationProperties({
		AppProperties.class, CorsProperties.class
})
@EnableJpaAuditing @EnableJpaRepositories(basePackageClasses = {
		PillRepository.class,
		PillPointRepository.class
})
@EnableMongoAuditing @EnableMongoRepositories(basePackageClasses = {
		PillElementRepository.class
})
public class PillApplication {

	public static void main(String[] args) {
		SpringApplication.run(PillApplication.class, args);
	}

}
