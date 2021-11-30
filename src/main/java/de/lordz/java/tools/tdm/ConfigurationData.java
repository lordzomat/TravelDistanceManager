package de.lordz.java.tools.tdm;


public class ConfigurationData {
	
	private String themeName;
	
	public static ConfigurationData LoadConfiguration() {
		return new ConfigurationData() {{setThemeName("com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme");}};
	}
	
	public String getThemeName() {
		return this.themeName;
	}
	
	public void setThemeName(String name) {
		this.themeName = name;
	}
}
