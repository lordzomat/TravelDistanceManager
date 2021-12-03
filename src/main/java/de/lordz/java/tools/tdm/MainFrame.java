package de.lordz.java.tools.tdm;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.DefaultTableCellRenderer;

import com.formdev.flatlaf.FlatLightLaf;
import com.google.common.base.Strings;

import de.lordz.java.tools.tdm.common.AppConstants;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.common.Logger;
import de.lordz.java.tools.tdm.config.AppConfiguration;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.SpringLayout;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 404497382974994431L;
	private static final DefaultTableCellRenderer rightTableCellRenderer = CreateTableCellRightRenderer();
	private AppConfiguration appConfiguration;
	private JPanel contentPane;
	private StatusBar statusBar;
	private JTable tableCustomers;
	private HashMap<Integer, EntityDataModelHelper<CustomerEntity>> customersColumnMap = createCustomersColumnMap();
	private JButton buttonNewCustomer;
	private JButton buttonEditCustomer;
	private JButton buttonDeleteCustomer;
	private JMenu fileMenuRecentDatabases;
	private CustomerBasicInfo customerBasicInfo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		FlatLightLaf.setup();
//		UIManager.put("TitlePane.menuBarEmbedded", false);
		FlatLightLaf.updateUI();
		IconFontSwing.register(FontAwesome.getIconFont());
		setLocale("de", "DE");
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
		setIconImage(IconFontSwing.buildImage(FontAwesome.ROAD, 20, Color.lightGray));
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mainMenuItemFile = new JMenu(LocalizationProvider.getString("mainframe.menuitem.file"));
		menuBar.add(mainMenuItemFile);
		
		JMenuItem fileMenuItemOpenDatabase = new JMenuItem(LocalizationProvider.getString("mainframe.menuitem.opendatabase"));
		fileMenuItemOpenDatabase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performOpenDatabase();
			}
		});
		mainMenuItemFile.add(fileMenuItemOpenDatabase);

		this.fileMenuRecentDatabases = new JMenu(LocalizationProvider.getString("mainframe.menuitem.recentdatabases"));
		mainMenuItemFile.add(this.fileMenuRecentDatabases);
		
		var fileMenuItemExit = new JMenuItem(LocalizationProvider.getString("mainframe.menuitem.exit"));
		fileMenuItemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mainMenuItemFile.add(fileMenuItemExit);
		

		JMenu mainMenuItemTheme = new JMenu(LocalizationProvider.getString("mainframe.menuitem.themes"));
		menuBar.add(mainMenuItemTheme);
		populateThemeMenuItems(mainMenuItemTheme);		
		
		contentPane = new JPanel();
//		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBorder(new EmptyBorder(5, 0, 5, 0));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panel.add(tabbedPane, BorderLayout.CENTER);
						
		JPanel panelCustomers = new JPanel();
		panelCustomers.setLayout(new BorderLayout(0, 0));
		tabbedPane.addTab(null, IconFontSwing.buildIcon(FontAwesome.USERS, 15, Color.lightGray), panelCustomers, LocalizationProvider.getString("mainframe.button.tooltip.customers"));
		
		JPanel panelTrips = new JPanel();
		tabbedPane.addTab(null, IconFontSwing.buildIcon(FontAwesome.CAR, 15, Color.lightGray), panelTrips, LocalizationProvider.getString("mainframe.button.tooltip.trips"));
		
		JPanel panelReport = new JPanel();
		tabbedPane.addTab(null, IconFontSwing.buildIcon(FontAwesome.FILE_TEXT_O, 15, Color.lightGray), panelReport, LocalizationProvider.getString("mainframe.button.tooltip.report"));
		
		JScrollPane scrollPane = new JScrollPane();
		panelCustomers.add(scrollPane, BorderLayout.WEST);
		
		tableCustomers = new JTable();
//		tableCustomers.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableCustomers.setShowGrid(true);
		tableCustomers.setColumnSelectionAllowed(false);
		tableCustomers.setDragEnabled(false);
		tableCustomers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableCustomers.getTableHeader().setReorderingAllowed(false);
		tableCustomers.getSelectionModel().addListSelectionListener(e -> processTableCustomerSelectionChanged(e));
		scrollPane.setViewportView(tableCustomers);
		scrollPane.setPreferredSize(new Dimension(400, 100));
		
		JPanel panelCustomersRight = new JPanel();
		panelCustomers.add(panelCustomersRight, BorderLayout.CENTER);
		panelCustomersRight.setLayout(new BorderLayout(0, 0));
		
		JToolBar toolBar = new JToolBar();
		panelCustomersRight.add(toolBar, BorderLayout.NORTH);
		
		this.buttonNewCustomer = new JButton(IconFontSwing.buildIcon(FontAwesome.PLUS_CIRCLE, 20, new Color(0, 150, 0)));
		this.buttonNewCustomer.setToolTipText(LocalizationProvider.getString("mainframe.button.new"));
		this.buttonNewCustomer.addActionListener(e -> openCustomerDialog(null));
		toolBar.add(this.buttonNewCustomer);
		
		this.buttonEditCustomer = new JButton(IconFontSwing.buildIcon(FontAwesome.PENCIL_SQUARE, 20, Color.ORANGE));
		this.buttonEditCustomer.setToolTipText(LocalizationProvider.getString("mainframe.button.edit"));
		this.buttonEditCustomer.addActionListener(e -> processCustomerEditDeleteClick(true));
		toolBar.add(this.buttonEditCustomer);
		
		this.buttonDeleteCustomer = new JButton(IconFontSwing.buildIcon(FontAwesome.MINUS_CIRCLE, 20, new Color(150, 0, 0)));
		this.buttonDeleteCustomer.setToolTipText(LocalizationProvider.getString("mainframe.button.delete"));
		this.buttonDeleteCustomer.addActionListener(e -> processCustomerEditDeleteClick(false));
		toolBar.add(this.buttonDeleteCustomer);
		
		JPanel panelCustomerInfo = new JPanel();
		panelCustomersRight.add(panelCustomerInfo, BorderLayout.CENTER);
		SpringLayout panelCustomerInfoSpringLayout = new SpringLayout();
		panelCustomerInfo.setLayout(panelCustomerInfoSpringLayout);
		
		this.customerBasicInfo = new CustomerBasicInfo();
		panelCustomerInfoSpringLayout.putConstraint(SpringLayout.NORTH, this.customerBasicInfo, 10, SpringLayout.NORTH, panelCustomerInfo);
		panelCustomerInfoSpringLayout.putConstraint(SpringLayout.WEST, this.customerBasicInfo, 10, SpringLayout.WEST, panelCustomerInfo);
		panelCustomerInfoSpringLayout.putConstraint(SpringLayout.SOUTH, this.customerBasicInfo, 382, SpringLayout.NORTH, panelCustomerInfo);
		panelCustomerInfoSpringLayout.putConstraint(SpringLayout.EAST, this.customerBasicInfo, 569, SpringLayout.WEST, panelCustomerInfo);
		panelCustomerInfo.add(this.customerBasicInfo);
		this.customerBasicInfo.setEditable(false);
		
		this.statusBar = StatusBar.addStatusbar(this);
		this.statusBar.setStatusMessage(LocalizationProvider.getString("mainframe.statusbar.nodatabaseopen"));
		setActionButtonsEnabledState(false);
		
		this.appConfiguration = AppConfiguration.loadAppConfiguration();
		if (this.appConfiguration != null) {
			if (!Strings.isNullOrEmpty(this.appConfiguration.SelectedTheme)) {
				setLookAndFeel(this.appConfiguration.SelectedTheme);
			}
		}
		
		reloadRecentDatabseMenu();
	}
	
	private void openCustomerDialog(CustomerEntity entity) {
		var cutomerDialog = new CustomerDialog();
		cutomerDialog.showDialog(entity, this);
		reloadCustomersTable();
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
			menuItem.addActionListener(e -> processThemeMenuItemClicked(e));
			mainMenuItem.add(menuItem);
		}
	}
	
	private void processThemeMenuItemClicked(ActionEvent event) {
		try {
			final var eventSource = event.getSource();
			if (eventSource != null && eventSource instanceof ThemeMenuItem) {
				var themeMenuItem = (ThemeMenuItem)eventSource;
				var themeName = themeMenuItem.getThemeName();
				if (this.appConfiguration != null) {
					this.appConfiguration.SelectedTheme = themeName;
					saveConfiguration();
				}
				
				setLookAndFeel(themeName);
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
	
	private void setActionButtonsEnabledState(boolean enaled) {
		this.buttonNewCustomer.setEnabled(enaled);
		this.buttonEditCustomer.setEnabled(enaled);
		this.buttonDeleteCustomer.setEnabled(enaled);
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
		            	statusBar.setStatusMessage(String.format(LocalizationProvider.getString("mainframe.statusbar.current_database"), databasePath));
		            	setActionButtonsEnabledState(true);
		            	addRecentDatabase(databasePath);
		            } else {
		            	setActionButtonsEnabledState(false);
		            	var messageTemplate = LocalizationProvider.getString("mainframe.error.opendatabase");
		            	var message = String.format(messageTemplate, databasePath);
		            	JOptionPane.showMessageDialog(null, message, LocalizationProvider.getString("mainframe.menuitem.opendatabase"), JOptionPane.ERROR_MESSAGE);
		            	statusBar.setStatusMessage(LocalizationProvider.getString("mainframe.statusbar.nodatabaseopen"));
		            }
		        } catch (InterruptedException ignore) {}
		        catch (java.util.concurrent.ExecutionException ex) {
		            Logger.Log(ex);
		        }
		    	
		    	waitDialog.close();
		    }
		};
		
		worker.execute();
		waitDialog.showDialog(LocalizationProvider.getString("mainframe.menuitem.opendatabase"), this);
	}
	
	private void performOpenDatabase() {
		try {
			final var fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			var filter = new FileNameExtensionFilter(LocalizationProvider.getString("mainframe.filechooser.databasefile"), "db");
			fileChooser.setFileFilter(filter);
			fileChooser.setAcceptAllFileFilterUsed(false);
			int result = fileChooser.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				final var databasePath = fileChooser.getSelectedFile().toString();
				openDatabase(databasePath);
	        }
		}
		catch (Exception ex) {
			Logger.Log(ex);
		}
	}
	
	private void openDatabase(String databasePath) {
		try {
			if (!Strings.isNullOrEmpty(databasePath) && new java.io.File(databasePath).exists()) {
				if (DatabaseProvider.getIsOpen()) {
					DatabaseProvider.closeDatabase();
				}
				
				openDatabaseAsync(databasePath, this.statusBar);
				reloadCustomersTable();
			}
		}catch (Exception ex) {
			Logger.Log(ex);
		}
	}
	
	private void reloadCustomersTable() {
		if (!DatabaseProvider.getIsOpen()) {
			return;
		}
		
		var customers = CustomerManager.getCustomers();
		tableCustomers.setModel(new EntityTableModel<CustomerEntity>(customers, this.customersColumnMap));
		var columnModel = tableCustomers.getColumnModel();
		if (columnModel != null && columnModel.getColumnCount() > 2) {
			var columnDistance = columnModel.getColumn(2);
			if (columnDistance != null) {
				columnDistance.setMaxWidth(100);
				columnDistance.setMinWidth(100);				
				columnDistance.setCellRenderer(rightTableCellRenderer);
			}
		}
	}
	
	private void processTableCustomerSelectionChanged(ListSelectionEvent event) {
		if (event != null && event.getValueIsAdjusting()) {
			return;
		}
		
		var entity = getSelectedCustomerEntity();
		fillCustomerInfo(entity == null ? new CustomerEntity() : entity);
	}
	
	private void processCustomerEditDeleteClick(boolean edit) {
		var entity = getSelectedCustomerEntity();
		if (entity != null && entity.getId() > 0) {
			if (edit) {
					openCustomerDialog(entity);
			} else {
				var message = String.format(LocalizationProvider.getString("mainframe.message.confirmcustomerdelete"), entity.getName());
				if (askForConfirmation(message, LocalizationProvider.getString("mainframe.button.delete"))) {
					DatabaseProvider.removeEntity(entity);
					reloadCustomersTable();
				}
			}
		} else {
			showErrorMessage(LocalizationProvider.getString("mainframe.message.nocustomerselected"));
		}
	}
	
	private CustomerEntity getSelectedCustomerEntity() {
		CustomerEntity entity = null;
		var selectedRowIndex = this.tableCustomers.getSelectedRow();
		if (selectedRowIndex >= 0) {
			var currentModel = tableCustomers.getModel();
			if (currentModel != null && currentModel instanceof EntityTableModel) {
				var entityTableModel = (EntityTableModel<CustomerEntity>)currentModel;
				if (entityTableModel != null) {
					entity = entityTableModel.getEntity(selectedRowIndex);
				}
			}
		}
		
		return entity;
	}
	
	private void fillCustomerInfo(CustomerEntity entity) {
		this.customerBasicInfo.fillFromEnity(entity);
	}
	
	private static HashMap<Integer, EntityDataModelHelper<CustomerEntity>> createCustomersColumnMap() {
		HashMap<Integer, EntityDataModelHelper<CustomerEntity>> columnMap = new HashMap<Integer, EntityDataModelHelper<CustomerEntity>>();
		columnMap.put(0, new EntityDataModelHelper<CustomerEntity>(LocalizationProvider.getString("customerdialog.label.name"), (entity) -> entity.getName()));
		columnMap.put(1, new EntityDataModelHelper<CustomerEntity>(LocalizationProvider.getString("customerdialog.label.city"), (entity) -> entity.getCity()));
		columnMap.put(2, new EntityDataModelHelper<CustomerEntity>(LocalizationProvider.getString("customerdialog.label.distance"), (entity) -> entity.getDistance()));
		return columnMap;
	}
	
	private static DefaultTableCellRenderer CreateTableCellRightRenderer() {
		var rightRenderer = new DefaultTableCellRenderer();
	    rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		return rightRenderer;
	}
	
	private void saveConfiguration() {
		if (!this.appConfiguration.saveConfiguration()) {
			showErrorMessage(String.format(LocalizationProvider.getString("mainframe.error.saveconfiguration"), AppConfiguration.ConfigurationFilePath));
		}
	}
	
	private void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(this, message, LocalizationProvider.getString("mainframe.menuitem.opendatabase"), JOptionPane.ERROR_MESSAGE);
	}
	
	private void addRecentDatabase(String databasePath) {
		try {
			if (!Strings.isNullOrEmpty(databasePath)) {
				var recentDatabases = this.appConfiguration.RecentDatabases;
				if (recentDatabases != null) {
					int currentIndex = recentDatabases.indexOf(databasePath);
					if (currentIndex >= 0) {
						recentDatabases.remove(databasePath);
					}
					
					recentDatabases.add(0, databasePath);
				}
				
				saveConfiguration();
				reloadRecentDatabseMenu();
			}
		} catch (Exception ex) {
			Logger.Log(ex);
		}
	}
	
	private void reloadRecentDatabseMenu() {
		try {
			var recentDatabases = this.appConfiguration.RecentDatabases;
			if (recentDatabases != null) {
				this.fileMenuRecentDatabases.removeAll();
				for (String databasePath : recentDatabases) {
					var menuItem = new JMenuItem(databasePath);
					menuItem.addActionListener(e -> openDatabase(databasePath));
					this.fileMenuRecentDatabases.add(menuItem);
				}
			}
		} catch (Exception ex) {
			Logger.Log(ex);
		}
	}
	
	private boolean askForConfirmation(String message, String title) {
		return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}
	
	private static void setLocale(String language, String country) {
		LocalizationProvider.setLocale(language, country);
	}
	
	private class ThemeEntry {
	    public final String displayName;
	    public final String className;
	    
	    public ThemeEntry(String displayName, String className) {
	    	this.displayName = displayName;
	    	this.className = className;
	    }
	}
}
