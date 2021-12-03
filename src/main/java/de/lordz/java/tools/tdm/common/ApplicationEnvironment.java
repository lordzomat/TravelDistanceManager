package de.lordz.java.tools.tdm.common;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Application environment.
 * 
 * @author lordz
 *
 */
public class ApplicationEnvironment {

	private static final String dataDirectoryName = ".tdm-data";
	private static final Path applicationDataDirectory = resolveApplicationDataDirectory();
	
	/**
	 * Retrieves the application data directory path.
	 * 
	 * @return The data directory path.
	 */
	public static Path getApplicationDataDirectory() {
		return applicationDataDirectory;
	}

	private static Path resolveApplicationDataDirectory() {
		Path path;
		var operatingSystemName = (System.getProperty("os.name")).toUpperCase();
		if (operatingSystemName.contains("WIN"))
		{
			path = Paths.get(System.getenv("AppData"), dataDirectoryName);
		}
		else
		{
			path = Paths.get(System.getProperty("user.home"), dataDirectoryName);
		}
		
		try {
			Files.createDirectories(path);
		} catch (Exception ex) {
			Logger.Log(ex);
		}
		
		return path;
	}
}
