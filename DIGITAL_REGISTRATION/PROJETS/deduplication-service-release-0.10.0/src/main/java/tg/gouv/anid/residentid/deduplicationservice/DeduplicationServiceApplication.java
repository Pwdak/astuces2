package tg.gouv.anid.residentid.deduplicationservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import tg.gouv.anid.residentid.deduplicationservice.config.FileExternalizationConfig;

@SpringBootApplication
@Slf4j
public class DeduplicationServiceApplication {

	public static void main(String[] args) {
		if (FileExternalizationConfig.creerLeDossierDeConfiguration()) {
			ConfigurableApplicationContext app = new SpringApplicationBuilder(
					DeduplicationServiceApplication.class)
					.build().run(FileExternalizationConfig.enhanceArgs(args));
			Environment env = app.getEnvironment();
			String protocol = "http";
			if (env.getProperty("server.ssl.key-store") != null) {
				protocol = "https";
			}
			log.info("\n----------------------------------------------------------\n\t"
							+ "Application '{}' is running! Access URLs:\n\t"
							+ "Local: \t\t{}://localhost:{}\n\t"
							+ "Profile(s): \t{}\n----------------------------------------------------------",
					env.getProperty("spring.application.name"),
					protocol,
					env.getProperty("server.port"),
					env.getActiveProfiles());
		}else {
			SpringApplication.run(DeduplicationServiceApplication.class, args);
		}
	}

}
