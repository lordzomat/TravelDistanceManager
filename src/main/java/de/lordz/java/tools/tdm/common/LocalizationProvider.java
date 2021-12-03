package de.lordz.java.tools.tdm.common;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Class to provide localization and internationalization stuff.
 * 
 * 
 * @author lordz
 *
 */
public class LocalizationProvider {

	private static ResourceBundle activeBundle;
//	private static Locale activeLocale;
	
	/**
	 * Sets the locale.
	 * 
	 * @param language The primary language e.g. en/de
	 * @param countrry The specific (sub) language to the country e.g. US,GB,DE
	 */
	public static void setLocale(String language, String country) {
		setLocale(new Locale(language, country));
	}
	
	/**
	 * Sets the locale.
	 * 
	 * @param locale The locale to set.
	 */
	public static void setLocale(Locale locale) {
//		activeLocale = locale;
		activeBundle = ResourceBundle.getBundle("de.lordz.java.tools.tdm.i18n.UiMessages", locale);
	}
	
	/**
	 * Gets the localized string.
	 * 
	 * @param key The key identifying the localized text.
	 * @return Returns the text in it's localized form.
	 */
	public static String getString(String key) {
		return activeBundle.getString(key);
	}
}
