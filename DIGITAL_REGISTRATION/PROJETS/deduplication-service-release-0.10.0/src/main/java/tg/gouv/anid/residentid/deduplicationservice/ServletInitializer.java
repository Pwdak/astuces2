package tg.gouv.anid.residentid.deduplicationservice;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import tg.gouv.anid.residentid.deduplicationservice.config.FileExternalizationConfig;

/**
 * @author Francis AHONSU
 */
public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

        return application.sources(DeduplicationServiceApplication.class)
                .properties(FileExternalizationConfig.getPropertiesConfig());
    }
}
