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
	private JPanel contentPane;
	private StatusBar statusBar;
	private JTable tableCustomers;
	private HashMap<Integer, EntityDataModelHelper<CustomerEntity>> customersColumnMap = createCustomersColumnMap();
	private JButton buttonNewCustomer;
	private JButton buttonEditCustomer;
	private JButton buttonDeleteCustomer;
	private CustomerBasicInfo customerBasicInfo;

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
		IconFontSwing.register(FontAwesome.getIconFont());
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
		setIconImage(IconFontSwing.buildImage(FontAwesome.ROAD, 20, Color.lightGray));
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
		// only for testing
		
		JMenu mainMenuItemTheme = new JMenu(LocalizationProvider.GetString("mainframe.menuitem.themes"));
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
		tabbedPane.addTab(null, IconFontSwing.buildIcon(FontAwesome.USERS, 15, Color.lightGray), panelCustomers, LocalizationProvider.GetString("mainframe.button.tooltip.customers"));
		
		JPanel panelTrips = new JPanel();
		tabbedPane.addTab(null, IconFontSwing.buildIcon(FontAwesome.CAR, 15, Color.lightGray), panelTrips, LocalizationProvider.GetString("mainframe.button.tooltip.trips"));
		
		JPanel panelReport = new JPanel();
		tabbedPane.addTab(null, IconFontSwing.buildIcon(FontAwesome.FILE_TEXT_O, 15, Color.lightGray), panelReport, LocalizationProvider.GetString("mainframe.button.tooltip.report"));
		
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
		this.buttonNewCustomer.setToolTipText(LocalizationProvider.GetString("mainframe.button.new"));
		this.buttonNewCustomer.addActionListener(e -> openCustomerDialog(null));
		toolBar.add(this.buttonNewCustomer);
		
		this.buttonEditCustomer = new JButton(IconFontSwing.buildIcon(FontAwesome.PENCIL_SQUARE, 20, Color.ORANGE));
		this.buttonEditCustomer.setToolTipText(LocalizationProvider.GetString("mainframe.button.edit"));
		this.buttonEditCustomer.addActionListener(e -> processCustomerEditDeleteClick(true));
		toolBar.add(this.buttonEditCustomer);
		
		this.buttonDeleteCustomer = new JButton(IconFontSwing.buildIcon(FontAwesome.MINUS_CIRCLE, 20, new Color(150, 0, 0)));
		this.buttonDeleteCustomer.setToolTipText(LocalizationProvider.GetString("mainframe.button.delete"));
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
		setActionButtonsEnabledState(false);
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
		            	statusBar.setMessage(String.format(LocalizationProvider.GetString("mainframe.statusbar.current_database"), databasePath));
		            	setActionButtonsEnabledState(true);
		            } else {
		            	setActionButtonsEnabledState(false);
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
	        } else {
	        	if (!new java.io.File(databasePath).exists()) {
	        		databasePath = null;;
	        		JOptionPane.showMessageDialog(this, "Database does not exist!", LocalizationProvider.GetString("mainframe.menuitem.opendatabase"), JOptionPane.ERROR_MESSAGE);
	        	}
	        }
			
			if (!Strings.isNullOrEmpty(databasePath)) {
				openDatabaseAsync(databasePath, this.statusBar);
				reloadCustomersTable();
			}
		}
		catch (Exception ex) {
			Logger.Log(ex);
		}
	}
	
	private void reloadCustomersTable() {
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
		if (edit) {
			var entity = getSelectedCustomerEntity();
			if (entity != null) {
				openCustomerDialog(entity);
			} else {
				// todo: show dialog, no customer selected
			}
		} else {
			// todo: implement
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
		columnMap.put(0, new EntityDataModelHelper<CustomerEntity>(LocalizationProvider.GetString("customerdialog.label.name"), (entity) -> entity.getName()));
		columnMap.put(1, new EntityDataModelHelper<CustomerEntity>(LocalizationProvider.GetString("customerdialog.label.city"), (entity) -> entity.getCity()));
		columnMap.put(2, new EntityDataModelHelper<CustomerEntity>(LocalizationProvider.GetString("customerdialog.label.distance"), (entity) -> entity.getDistance()));
		return columnMap;
	}
	
	private static DefaultTableCellRenderer CreateTableCellRightRenderer() {
		var rightRenderer = new DefaultTableCellRenderer();
	    rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		return rightRenderer;
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
