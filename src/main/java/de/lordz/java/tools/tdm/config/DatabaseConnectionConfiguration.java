package de.lordz.java.tools.tdm.config;

import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.lordz.java.tools.tdm.common.Logger;

/**
 * Provides database configuration e.g. for PostgreSQL.
 * 
 * @author lordzomat
 *
 */
public class DatabaseConnectionConfiguration {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public String Server;
    public String DatabaseName;
    public String User;
    public String Password;
    
    /**
     * Loads the configuration.
     * 
     * @return The configuration if one exists, otherwise a new configuration.
     */
    public static DatabaseConnectionConfiguration loadFromFile(Path path ) {
        DatabaseConnectionConfiguration configuration = null;
        try {
            if (path != null && Files.exists(path)) {
                configuration = objectMapper.readValue(path.toFile(), DatabaseConnectionConfiguration.class);
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        return configuration != null ? configuration : new DatabaseConnectionConfiguration();
    }
}
