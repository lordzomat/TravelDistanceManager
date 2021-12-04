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
import de.lordz.java.tools.tdm.common.DateTimeHelper;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.common.Logger;
import de.lordz.java.tools.tdm.config.AppConfiguration;
import de.lordz.java.tools.tdm.entities.CustomerEntity;
import de.lordz.java.tools.tdm.entities.TripEntity;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
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
    private JTable tableTrips;
    private HashMap<Integer, EntityDataModelHelper<CustomerEntity>> customersColumnMap = createCustomersColumnMap();
    private HashMap<Integer, EntityDataModelHelper<TripEntity>> tripsColumnMap = createTripsColumnMap();
    private JButton buttonNewCustomer;
    private JButton buttonEditCustomer;
    private JButton buttonDeleteCustomer;
    private JButton buttonNewTrip;
    private JButton buttonEditTrip;
    private JButton buttonDeleteTrip;
    private JMenu fileMenuRecentDatabases;
    private HashMap<Integer, CustomerEntity> currentCustomers;
    private CustomerBasicInfo customerBasicInfo;
    private TripBasicInfo tripBasicInfo;

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

        JMenuItem fileMenuItemOpenDatabase = new JMenuItem(
                LocalizationProvider.getString("mainframe.menuitem.opendatabase"));
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

        /*** Customers ****/
        
        JPanel panelCustomers = new JPanel();
        panelCustomers.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab(null, IconFontSwing.buildIcon(FontAwesome.USERS, 15, Color.lightGray), panelCustomers,
                LocalizationProvider.getString("mainframe.button.tooltip.customers"));

        JScrollPane scrollPaneCustomers = new JScrollPane();
        panelCustomers.add(scrollPaneCustomers, BorderLayout.WEST);

        this.tableCustomers = new JTable();
//		tableCustomers.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.tableCustomers.setShowGrid(true);
        this.tableCustomers.setColumnSelectionAllowed(false);
        this.tableCustomers.setDragEnabled(false);
        this.tableCustomers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.tableCustomers.getTableHeader().setReorderingAllowed(false);
        this.tableCustomers.getSelectionModel().addListSelectionListener(e -> processTableCustomerSelectionChanged(e));
        scrollPaneCustomers.setViewportView(this.tableCustomers);
        scrollPaneCustomers.setPreferredSize(new Dimension(400, 100));

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
        panelCustomerInfoSpringLayout.putConstraint(SpringLayout.NORTH, this.customerBasicInfo, 10, SpringLayout.NORTH,
                panelCustomerInfo);
        panelCustomerInfoSpringLayout.putConstraint(SpringLayout.WEST, this.customerBasicInfo, 10, SpringLayout.WEST,
                panelCustomerInfo);
        panelCustomerInfoSpringLayout.putConstraint(SpringLayout.SOUTH, this.customerBasicInfo, 382, SpringLayout.NORTH,
                panelCustomerInfo);
        panelCustomerInfoSpringLayout.putConstraint(SpringLayout.EAST, this.customerBasicInfo, 569, SpringLayout.WEST,
                panelCustomerInfo);
        panelCustomerInfo.add(this.customerBasicInfo);
        this.customerBasicInfo.setEditable(false);
        
        /*** Trips ****/
        
        JPanel panelTrips = new JPanel();
        tabbedPane.addTab(null, IconFontSwing.buildIcon(FontAwesome.CAR, 15, Color.lightGray), panelTrips,
                LocalizationProvider.getString("mainframe.button.tooltip.trips"));
        panelTrips.setLayout(new BorderLayout(0, 0));
        
        JScrollPane scrollPaneTrips = new JScrollPane();
        panelTrips.add(scrollPaneTrips, BorderLayout.WEST);
        this.tableTrips = new JTable();
        this.tableTrips.setShowGrid(true);
        this.tableTrips.setColumnSelectionAllowed(false);
        this.tableTrips.setDragEnabled(false);
        this.tableTrips.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.tableTrips.getTableHeader().setReorderingAllowed(false);
        this.tableTrips.getSelectionModel().addListSelectionListener(e -> processTableTripsSelectionChanged(e));
        scrollPaneTrips.setViewportView(this.tableTrips);
        scrollPaneTrips.setPreferredSize(new Dimension(400, 100));
        
        JPanel panelTripsRight = new JPanel();
        panelTrips.add(panelTripsRight, BorderLayout.CENTER);
        panelTripsRight.setLayout(new BorderLayout(0, 0));
        
        JToolBar toolBarTrips = new JToolBar();
        panelTripsRight.add(toolBarTrips, BorderLayout.NORTH);
        
        this.buttonNewTrip = new JButton(IconFontSwing.buildIcon(FontAwesome.PLUS_CIRCLE, 20, new Color(0, 150, 0)));
        this.buttonNewTrip.setToolTipText(LocalizationProvider.getString("mainframe.button.new"));
        this.buttonNewTrip.addActionListener(e -> openTripDialog(null));
        toolBarTrips.add(this.buttonNewTrip);
        
        this.buttonEditTrip = new JButton(IconFontSwing.buildIcon(FontAwesome.PENCIL_SQUARE, 20, Color.ORANGE));
        this.buttonEditTrip.setToolTipText(LocalizationProvider.getString("mainframe.button.edit"));
        this.buttonEditTrip.addActionListener(e -> processTripEditDeleteClick(true));
        toolBarTrips.add(this.buttonEditTrip);
        
        this.buttonDeleteTrip = new JButton(IconFontSwing.buildIcon(FontAwesome.MINUS_CIRCLE, 20, new Color(150, 0, 0)));
        this.buttonDeleteTrip.setToolTipText(LocalizationProvider.getString("mainframe.button.delete"));
        this.buttonDeleteTrip.addActionListener(e -> processTripEditDeleteClick(false));
        toolBarTrips.add(this.buttonDeleteTrip);
        
        JPanel panelTripsInfo = new JPanel();
        panelTripsRight.add(panelTripsInfo, BorderLayout.CENTER);
        panelTripsInfo.setLayout(null);
        
        this.tripBasicInfo = new TripBasicInfo();
        this.tripBasicInfo.setBounds(10, 11, 559, 298);
        this.tripBasicInfo.setEditable(false);
        panelTripsInfo.add(this.tripBasicInfo);
        
        /*** Reports ****/
        
        JPanel panelReport = new JPanel();
        tabbedPane.addTab(null, IconFontSwing.buildIcon(FontAwesome.FILE_TEXT_O, 15, Color.lightGray), panelReport,
                LocalizationProvider.getString("mainframe.button.tooltip.report"));
        
        /*** Common ****/

        this.statusBar = StatusBar.addStatusbar(this);
        this.statusBar.setStatusMessage(LocalizationProvider.getString("mainframe.statusbar.nodatabaseopen"));
        setActionButtonsEnabledState(false);
        setTripActionButtonsEnabledState(false);

        this.appConfiguration = AppConfiguration.loadAppConfiguration();
        if (this.appConfiguration != null) {
            if (!Strings.isNullOrEmpty(this.appConfiguration.SelectedTheme)) {
                setLookAndFeel(this.appConfiguration.SelectedTheme);
            }
        }

        reloadRecentDatabaseMenu();
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
                new ThemeEntry("GitHub Dark",
                        "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme"),
                new ThemeEntry("Nimbus", NimbusLookAndFeel.class.getName()),
                new ThemeEntry("Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel") };
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
                var themeMenuItem = (ThemeMenuItem) eventSource;
                var themeName = themeMenuItem.getThemeName();
                if (this.appConfiguration != null) {
                    this.appConfiguration.SelectedTheme = themeName;
                    saveConfiguration();
                }

                setLookAndFeel(themeName);
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }
    }

    private static void setLookAndFeel(String className) {
        try {
            if (!Strings.isNullOrEmpty(className)) {
                UIManager.setLookAndFeel(className);
                FlatLightLaf.updateUI();
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }
    }

    private void setActionButtonsEnabledState(boolean enaled) {
        this.buttonNewCustomer.setEnabled(enaled);
        this.buttonEditCustomer.setEnabled(enaled);
        this.buttonDeleteCustomer.setEnabled(enaled);
    }
    
    private void setTripActionButtonsEnabledState(boolean enaled) {
        this.buttonNewTrip.setEnabled(enaled);
        this.buttonEditTrip.setEnabled(enaled);
        this.buttonDeleteTrip.setEnabled(enaled);
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
                        statusBar.setStatusMessage(String.format(
                                LocalizationProvider.getString("mainframe.statusbar.current_database"), databasePath));
                        setActionButtonsEnabledState(true);
                        addRecentDatabase(databasePath);
                    } else {
                        setActionButtonsEnabledState(false);
                        var messageTemplate = LocalizationProvider.getString("mainframe.error.opendatabase");
                        var message = String.format(messageTemplate, databasePath);
                        showErrorMessage(message, LocalizationProvider.getString("mainframe.menuitem.opendatabase"));
                        statusBar.setStatusMessage(LocalizationProvider.getString("mainframe.statusbar.nodatabaseopen"));
                    }
                } catch (InterruptedException ignore) {
                } catch (java.util.concurrent.ExecutionException ex) {
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
            var filter = new FileNameExtensionFilter(
                    LocalizationProvider.getString("mainframe.filechooser.databasefile"), "db");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                final var databasePath = fileChooser.getSelectedFile().toString();
                openDatabase(databasePath);
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }
    }

    private void openDatabase(String databasePath) {
        try {
            if (!Strings.isNullOrEmpty(databasePath)) {
                if (DatabaseProvider.getIsOpen()) {
                    DatabaseProvider.closeDatabase();
                }

                openDatabaseAsync(databasePath, this.statusBar);
                reloadCustomersTable();
                reloadTripTable();
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }
    }

    private void reloadCustomersTable() {
        if (!DatabaseProvider.getIsOpen()) {
            return;
        }

        var customers = CustomerManager.getCustomers();
        this.currentCustomers = createCustomersHashMap(customers);
        if (customers != null) {
            setTripActionButtonsEnabledState(customers.size() > 0);
            this.tripBasicInfo.setCustomers(customers);
            this.tableCustomers.setModel(new EntityTableModel<CustomerEntity>(customers, this.customersColumnMap));
            var columnModel = this.tableCustomers.getColumnModel();
            if (columnModel != null && columnModel.getColumnCount() > 2) {
                var columnDistance = columnModel.getColumn(2);
                if (columnDistance != null) {
                    columnDistance.setMaxWidth(100);
                    columnDistance.setMinWidth(100);
                    columnDistance.setCellRenderer(rightTableCellRenderer);
                }
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
        var title = edit ? LocalizationProvider.getString("mainframe.button.edit")
                : LocalizationProvider.getString("mainframe.button.delete");
        if (entity != null && entity.getId() > 0) {
            if (edit) {
                openCustomerDialog(entity);
            } else {
                var message = String.format(LocalizationProvider.getString("mainframe.message.confirmcustomerdelete"), entity.getName());

                if (askForConfirmation(message, title)) {
                    if (!TripManager.checkIsCustomerAssignd(entity.getId())) {
                        entity.setDeleted();
                        DatabaseProvider.updateEntity(entity);
                        reloadCustomersTable();
                    } else {
                        showErrorMessage(LocalizationProvider.getString("customerdialog.message.deletenotpossiblestillinuse"), title);
                    }
                }
            }
        } else {
            showErrorMessage(LocalizationProvider.getString("mainframe.message.nocustomerselected"), title);
        }
    }

    private CustomerEntity getSelectedCustomerEntity() {
        CustomerEntity entity = null;
        var selectedRowIndex = this.tableCustomers.getSelectedRow();
        if (selectedRowIndex >= 0) {
            var currentModel = this.tableCustomers.getModel();
            if (currentModel != null && currentModel instanceof EntityTableModel) {
                var entityTableModel = (EntityTableModel<CustomerEntity>) currentModel;
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
        columnMap.put(0, new EntityDataModelHelper<CustomerEntity>(
                LocalizationProvider.getString("customerdialog.label.name"), (entity) -> entity.getName()));
        columnMap.put(1, new EntityDataModelHelper<CustomerEntity>(
                LocalizationProvider.getString("customerdialog.label.city"), (entity) -> entity.getCity()));
        columnMap.put(2, new EntityDataModelHelper<CustomerEntity>(
                LocalizationProvider.getString("customerdialog.label.distance"), (entity) -> entity.getDistance()));
        return columnMap;
    }

    private static DefaultTableCellRenderer CreateTableCellRightRenderer() {
        var rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        return rightRenderer;
    }

    private void saveConfiguration() {
        if (!this.appConfiguration.saveConfiguration()) {
            showErrorMessage(String.format(LocalizationProvider.getString("mainframe.error.saveconfiguration"),
                    AppConfiguration.ConfigurationFilePath));
        }
    }

    private void showErrorMessage(String message) { 
        showErrorMessage(message, AppConstants.ApplicationName);
    }
    
    private void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
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
                reloadRecentDatabaseMenu();
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }
    }

    private void reloadRecentDatabaseMenu() {
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
    
    private HashMap<Integer, EntityDataModelHelper<TripEntity>> createTripsColumnMap() {
        HashMap<Integer, EntityDataModelHelper<TripEntity>> columnMap = new HashMap<Integer, EntityDataModelHelper<TripEntity>>();
        columnMap.put(0, new EntityDataModelHelper<TripEntity>(
                LocalizationProvider.getString("tripbasicinfo.label.date"), (entity) -> DateTimeHelper.toDisplayDateFormat(entity.getLocalDate())));
        columnMap.put(1, new EntityDataModelHelper<TripEntity>(
                LocalizationProvider.getString("tripbasicinfo.label.time"), (entity) -> DateTimeHelper.toDisplayShortTimeFormat(entity.getLocalTime())));
        columnMap.put(2, new EntityDataModelHelper<TripEntity>(
                LocalizationProvider.getString("tripbasicinfo.label.customer"), (entity) -> lookupCustomerName(entity.getCustomerId())));
        return columnMap;
    }
    
    private String lookupCustomerName(int customerId) {
        var customers = this.currentCustomers;       
        if (customers != null && customers.containsKey(customerId)) {
            var customer = customers.get(customerId);
            if (customer != null) {
                return customer.getName();
            }            
        }
        
        return "";
    }
    
    private void openTripDialog(TripEntity entity) {
        var tripDialog = new TripDialog(this.currentCustomers.values());
        tripDialog.showDialog(entity, this);
        reloadTripTable();
    }
   
    private void reloadTripTable() {
        if (!DatabaseProvider.getIsOpen()) {
            return;
        }

        var trips = TripManager.getTrips();    
        this.tableTrips.setModel(new EntityTableModel<TripEntity>(trips, this.tripsColumnMap));
        var columnModel = this.tableTrips.getColumnModel();
        if (columnModel != null && columnModel.getColumnCount() > 1) {
            var columnDate = columnModel.getColumn(0);
            if (columnDate != null) {
                columnDate.setMaxWidth(80);
                columnDate.setMinWidth(80);
            }
            
            var columnTime = columnModel.getColumn(1);
            if (columnTime != null) {
                columnTime.setMaxWidth(60);
                columnTime.setMinWidth(60);
            }
        }
    }
    
    private void processTableTripsSelectionChanged(ListSelectionEvent event) {
        if (event != null && event.getValueIsAdjusting()) {
            return;
        }

        var entity = getSelectedTripEntity();
        this.tripBasicInfo.fillFromEnity((entity == null ? new TripEntity() : entity));
    }
    
    private TripEntity getSelectedTripEntity() {
        TripEntity entity = null;
        var selectedRowIndex = this.tableTrips.getSelectedRow();
        if (selectedRowIndex >= 0) {
            var currentModel = this.tableTrips.getModel();
            if (currentModel != null && currentModel instanceof EntityTableModel) {
                var entityTableModel = (EntityTableModel<TripEntity>)currentModel;
                if (entityTableModel != null) {
                    entity = entityTableModel.getEntity(selectedRowIndex);
                }
            }
        }

        return entity;
    }
    
    private void processTripEditDeleteClick(boolean edit) {
        var entity = getSelectedTripEntity();
        var title = edit ? LocalizationProvider.getString("mainframe.button.edit")
                : LocalizationProvider.getString("mainframe.button.delete");
        if (entity != null && entity.getId() > 0) {
            if (edit) {
                openTripDialog(entity);
            } else {
                var message = String.format(LocalizationProvider.getString("mainframe.message.confirmtripdelete"), 
                        DateTimeHelper.toDisplayDateFormat(entity.getLocalDate()), 
                        DateTimeHelper.toDisplayShortTimeFormat(entity.getLocalTime()));
                if (askForConfirmation(message, LocalizationProvider.getString("mainframe.button.delete"))) {
                    entity.setDeleted();
                    DatabaseProvider.updateEntity(entity);
                    reloadTripTable();
                }
            }
        } else {
            showErrorMessage(LocalizationProvider.getString("mainframe.message.notripselected"), title);
        }
    }
    
    private HashMap<Integer, CustomerEntity> createCustomersHashMap(List<CustomerEntity> customers) {
        if (customers != null) {
            var hashMap = new HashMap<Integer, CustomerEntity>(customers.size());
            for (var customer : customers) {
                if (customer != null) {
                    hashMap.put(customer.getId(), customer);
                }
            }
            
            return hashMap;
        }
        
        return new HashMap<Integer, CustomerEntity>(0);
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
