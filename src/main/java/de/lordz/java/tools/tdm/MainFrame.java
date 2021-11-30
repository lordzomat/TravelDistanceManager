package de.lordz.java.tools.tdm;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import com.formdev.flatlaf.FlatLightLaf;
import com.google.common.base.Strings;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 404497382974994431L;
	private JPanel contentPane;
	private StatusBar statusBar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		FlatLightLaf.setup();
//		UIManager.put("TitlePane.menuBarEmbedded", false);
		var configurationData = ConfigurationData.LoadConfiguration();
		if (configurationData != null ) {
			setLookAndFeel(configurationData.getThemeName());
		}
		FlatLightLaf.updateUI();
		LocalizationProvider.setLocale("de", "DE");
	    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	            DatabaseProvider.closeDatabase();
	        }
	    }, "ApplicationCloseThread"));
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {					
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 600);
		setTitle(String.format("%s (v%s)", AppConstants.ApplicationName, AppConstants.ApplicationVersion));

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mainMenuItemFile = new JMenu(LocalizationProvider.GetString("mainframe.menuitem.file"));
		menuBar.add(mainMenuItemFile);
		
		JMenuItem fileMenuItemOpenDatabase = new JMenuItem(LocalizationProvider.GetString("mainframe.menuitem.opendatabase"));
		fileMenuItemOpenDatabase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performOpenDatabase();
			}
		});
		mainMenuItemFile.add(fileMenuItemOpenDatabase);
		
		JMenuItem fileMenuItemExit = new JMenuItem(LocalizationProvider.GetString("mainframe.menuitem.exit"));
		fileMenuItemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mainMenuItemFile.add(fileMenuItemExit);
		
		JMenu mainMenuItemTheme = new JMenu(LocalizationProvider.GetString("mainframe.menuitem.themes"));
		menuBar.add(mainMenuItemTheme);
		populateThemeMenuItems(mainMenuItemTheme);		
		
		contentPane = new JPanel();
//		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBorder(new EmptyBorder(5, 0, 5, 0));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		getContentPane().setLayout(new BorderLayout());
		this.statusBar = StatusBar.addStatusbar(this);
	}
	
	private void populateThemeMenuItems(JMenu mainMenuItem) {
		
		var availableThemes = new ThemeEntry[] { 
				new ThemeEntry("Light Flat", "com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme"),
				new ThemeEntry("Dark Flat", "com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme"),
				new ThemeEntry("GitHub", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubIJTheme"),
				new ThemeEntry("GitHub Dark", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme"),
				new ThemeEntry("Nimbus", NimbusLookAndFeel.class.getName()),
				new ThemeEntry("Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel")
				};
		for (ThemeEntry themeEntry : availableThemes) {
			final var menuItem = new ThemeMenuItem(themeEntry.displayName, themeEntry.className);
			menuItem.addActionListener(e -> themeMenuItemActionPerformed(e));
			mainMenuItem.add(menuItem);
		}
	}
	
	private void themeMenuItemActionPerformed(ActionEvent event) {
		try {
			final var eventSource = event.getSource();
			if (eventSource != null && eventSource instanceof ThemeMenuItem) {
				var themeMenuItem = (ThemeMenuItem)eventSource;
				setLookAndFeel(themeMenuItem.getThemeName());
			}
		}
		catch (Exception ex) {
			Logger.Log(ex);
		}
	}
	
	private static void setLookAndFeel(String className) {
		try {
			if (!Strings.isNullOrEmpty(className)) {
				UIManager.setLookAndFeel(className);
				FlatLightLaf.updateUI();
			}
		}
		catch (Exception ex) {
			Logger.Log(ex);
		}
	}
	
	private void openDatabaseAsync(String databasePath, StatusBar statusBar) {
		var waitDialog = new WaitDialog();
		var worker = new SwingWorker<Boolean, Void>() {
		    @Override
		    public Boolean doInBackground() {
		    	return DatabaseProvider.openDatabase(databasePath);
		    }

		    @Override
		    public void done() {
		    	try {
		            if (get()) {
		            	statusBar.setMessage(String.format(LocalizationProvider.GetString("mainframe.statusbar.current_database"), databasePath));
		            } else {
		            	var messageTemplate = LocalizationProvider.GetString("mainframe.error.opendatabase");
		            	var message = String.format(messageTemplate, databasePath);
		            	JOptionPane.showMessageDialog(null, message, LocalizationProvider.GetString("mainframe.menuitem.opendatabase"), JOptionPane.ERROR_MESSAGE);
		            }
		        } catch (InterruptedException ignore) {}
		        catch (java.util.concurrent.ExecutionException ex) {
		            Logger.Log(ex);
		        }
		    	
		    	waitDialog.close();
		    }
		};
		
		worker.execute();
		waitDialog.showDialog(LocalizationProvider.GetString("mainframe.menuitem.opendatabase"), this);
	}
	
	private void performOpenDatabase() {
		try {
			final var fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			var filter = new FileNameExtensionFilter("Database file", "db");
			fileChooser.setFileFilter(filter);
			fileChooser.setAcceptAllFileFilterUsed(false);
			int result = fileChooser.showOpenDialog(this);
			String databasePath = "Q:\\Java\\IDE\\data\\tdm.db"; // todo: fallback for development versions, replace with dev configuration file read later
			if (result == JFileChooser.APPROVE_OPTION) {
				databasePath = fileChooser.getSelectedFile().toString();			
	        }
			
			openDatabaseAsync(databasePath, this.statusBar);
//			if (DatabaseProvider.openDatabase(databasePath)) {
//				this.statusBar.setMessage(String.format("Current database: %s", databasePath));	
//			}
		}
		catch (Exception ex) {
			Logger.Log(ex);
		}
	}
	
	public class ThemeEntry {
	    public final String displayName;
	    public final String className;
	    
	    public ThemeEntry(String displayName, String className) {
	    	this.displayName = displayName;
	    	this.className = className;
	    }
	}

}
