package de.lordz.java.tools.tdm.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.lordz.java.tools.tdm.common.ApplicationEnvironment;
import de.lordz.java.tools.tdm.common.Logger;

/**
 * Represents the application configuration (JSON file).
 * 
 * @author lordz
 *
 */
public class AppConfiguration {

	private static final String configurationFileName = "tdm.json";
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static final Path ConfigurationFilePath = getConfigurationFilePath();
	
	public List<String> RecentDatabases = new ArrayList<String>();
	
	public String SelectedTheme;
	
	/**
	 * Loads the configuration.
	 * 
	 * @return The configuration if one exists, otherwise a new configuration.
	 */
	public static AppConfiguration loadAppConfiguration() {		
		AppConfiguration configuration = null;
		try {
			if (ConfigurationFilePath != null && Files.exists(ConfigurationFilePath)) {
				configuration = objectMapper.readValue(ConfigurationFilePath.toFile(), AppConfiguration.class);
			}
		}
		catch (Exception ex) {
			Logger.Log(ex);
		}
		
		return configuration != null ? configuration : new AppConfiguration();
	}
	
	/**
	 * Saves the current configuration to the configuration file path.
	 * 
	 * @return Returns true on success, otherwise false.
	 */
	public boolean saveConfiguration() {
		boolean result = false;
		try {
			if (ConfigurationFilePath != null) {
				File configurationFile = ConfigurationFilePath.toFile();
				boolean writeConfiguration = true;
				if (!configurationFile.exists()) {
					writeConfiguration = configurationFile.createNewFile();
				}
				
				if (writeConfiguration) {
					objectMapper.writeValue(configurationFile, this);
					result = true;
				}
			}
		}
		catch (Exception ex) {
			Logger.Log(ex);
		}
		
		return result;
	}
	
	private static Path getConfigurationFilePath() {
		return Paths.get(ApplicationEnvironment.getApplicationDataDirectory().toString(), configurationFileName);
	}
}
