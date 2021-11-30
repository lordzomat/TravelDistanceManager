package de.lordz.java.tools.tdm;

/**
 * Class to wrap all logging done in the application.
 * 
 * @author lordz
 * 
 */
public final class Logger {
	public static void Log(Exception ex) {
		String message = null;
        var cause = ex.getCause();
        if (cause != null) {
            message = cause.getMessage();
        } else {
            message = ex.getMessage();
        }
		
		System.out.println("Exception: " + message);
		ex.printStackTrace();
	}
	
	public static void LogVerbose(String message, Object... args) {		
		Log(TraceLevel.Verbose, message, args);
	}
	
	public static void LogInformation(String message, Object... args) {		
		Log(TraceLevel.Information, message, args);
	}
	
	public static void LogWarning(String message, Object... args) {		
		Log(TraceLevel.Warning, message, args);
	}
	
	public static void LogError(String message, Object... args) {		
		Log(TraceLevel.Error, message, args);
	}
	
	private static void Log(TraceLevel traceLevel, String message, Object... args) {		
		if (args != null && args.length > 0) {
			message = String.format(message, args);
		}
		
		System.out.println(String.format("<%s> %s", traceLevel, message));
	}
}
