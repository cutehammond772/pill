package me.cutehammond.pill;

import me.cutehammond.pill.global.config.properties.AppProperties;
import me.cutehammond.pill.global.config.properties.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties({
		AppProperties.class, CorsProperties.class
})
public class PillApplication {

	public static void main(String[] args) {
		SpringApplication.run(PillApplication.class, args);
	}

}
