package tg.gouv.anid.residentid.deduplicationservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author Francis AHONSU
 */
@Slf4j
public class FileExternalizationConfig {

    private FileExternalizationConfig() {
        //
    }
    private static final String NOM_DU_FICHIER_DE_LAPPLICATION_CONTEXT_PROD = "application.yml";
    private static final String DOSSIER_CONFIG = ".residentid-deduplication-service" + File.separator + "conf";

    private static String getPropertiesConfigPath() {
        return System.getProperty("user.home")
                + File.separator + DOSSIER_CONFIG
                + File.separator + NOM_DU_FICHIER_DE_LAPPLICATION_CONTEXT_PROD;
    }

    public static Map<String, Object> getPropertiesConfig() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.config.location", "classpath:/,file:${user.home}/" + DOSSIER_CONFIG + "/");
        return properties;
    }

    public static String[] enhanceArgs(String[] args) {
        final String location = "--spring.config.location=classpath:/," +
                "file:${user.home}/" + DOSSIER_CONFIG + "/";
        if (args.length == 0) {
            return new String[]{location};
        }
        if (!String.join("", args)
                .contains("spring.config.location")) {
            String[] result = Arrays.copyOf(args, args.length + 1);
            result[result.length - 1] = location;
            return result;
        }
        return args;
    }

    public static boolean creerLeDossierDeConfiguration() {
        File fichierDeConfiguration = new File(FileExternalizationConfig.getPropertiesConfigPath());
        if (!fichierDeConfiguration.exists() || !fichierDeConfiguration.isFile()) {
            String errorMsg = "Le fichier de configuration n'a pas pu être créé automatiquement. Raison : %s";
            if (fichierDeConfiguration.getParentFile().mkdirs()) {
                try {
                    Path source = Paths.get(new ClassPathResource(FileExternalizationConfig
                            .NOM_DU_FICHIER_DE_LAPPLICATION_CONTEXT_PROD).getURI());
                    Path destination = Paths.get(fichierDeConfiguration.toURI());
                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    log.info(fichierDeConfiguration.getAbsolutePath() + " créé avec succès!!!");
                    return true;
                } catch (IOException e) {
                    log.error(String.format(errorMsg, e.getMessage()));
                }
            } else {
                log.error(String.format(errorMsg, "Impossible de créer le dossier"));
            }
        } else {
            return true;
        }
        return false;
    }
}
