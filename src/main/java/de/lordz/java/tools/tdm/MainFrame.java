package de.lordz.java.tools.tdm;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.DefaultTableCellRenderer;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.github.lgooddatepicker.components.DatePicker;
import com.google.common.base.Strings;
import de.lordz.java.tools.tdm.common.AppConstants;
import de.lordz.java.tools.tdm.common.DateTimeHelper;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.common.Logger;
import de.lordz.java.tools.tdm.config.AppConfiguration;
import de.lordz.java.tools.tdm.config.ReportConfiguration;
import de.lordz.java.tools.tdm.entities.*;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SpringLayout;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 404497382974994431L;
    private static final DefaultTableCellRenderer RightTableCellRenderer = CreateTableCellRightRenderer();
    private final Icon selectedThemeCheckIcon;
    private AppConfiguration appConfiguration;
    private JPanel contentPane;
    private JMenu mainMenuItemTheme;
    private StatusBar statusBar;
    private JTable tableCustomers;
    private JTable tableTrips;
    private JTable tableTripTypes;
    private HashMap<Integer, EntityDataModelHelper<Customer>> customersColumnMap = createCustomersColumnMap();
    private HashMap<Integer, EntityDataModelHelper<Trip>> tripsColumnMap = createTripsColumnMap();
    private HashMap<Integer, EntityDataModelHelper<TripType>> tripTypesColumnMap = createTripTypesColumnMap();
    private JButton buttonNewCustomer;
    private JButton buttonEditCustomer;
    private JButton buttonDeleteCustomer;
    private JButton buttonNewTrip;
    private JButton buttonEditTrip;
    private JButton buttonDeleteTrip;
    private JToggleButton toggleButtonTripFilterEnabled;
    private DatePicker datePickerTripFilterStart;
    private DatePicker datePickerTripFilterEnd;
    private JButton buttonNewTripType;
    private JButton buttonEditTripType;
    private JButton buttonDeleteTripType;    
    private JMenu fileMenuRecentDatabases;
    private HashMap<Integer, Customer> currentCustomers;
    private HashMap<Integer, TripType> currentTripTypes;
    private CustomerBasicInfo customerBasicInfo;
    private TripBasicInfo tripBasicInfo;
    private TripTypeBasicInfo tripTypeBasicInfo;
    private ReportPanel reportPanel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        FlatLightLaf.setup();
//		UIManager.put("TitlePane.menuBarEmbedded", false);
//        UIManager.put("Table.intercellSpacing", new Dimension( 1, 1 ));
        FlatLaf.updateUI();
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
        this.selectedThemeCheckIcon = IconFontSwing.buildIcon(FontAwesome.CHECK, 15, Color.GRAY);
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

        this.mainMenuItemTheme = new JMenu(LocalizationProvider.getString("mainframe.menuitem.themes"));
        menuBar.add(this.mainMenuItemTheme);

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
        toolBar.setFloatable(false);
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
        toolBarTrips.setFloatable(false);
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
        
        this.toggleButtonTripFilterEnabled = new JToggleButton(IconFontSwing.buildIcon(FontAwesome.FILTER, 20, new Color(0, 145, 255)));
        this.toggleButtonTripFilterEnabled.setSelected(true);
        this.toggleButtonTripFilterEnabled.setToolTipText(LocalizationProvider.getString("mainframe.button.tooltip.tripFilter"));
        this.toggleButtonTripFilterEnabled.addItemListener(e -> processTripFilterInputChanged(true));
        toolBarTrips.add(this.toggleButtonTripFilterEnabled);
        
        this.datePickerTripFilterStart = DateTimeHelper.createDatePicker();
        this.datePickerTripFilterStart.setDate(LocalDate.of(LocalDate.now().getYear(), 1, 1));
        this.datePickerTripFilterStart.addDateChangeListener(e -> processTripFilterInputChanged(false));
        var panelDatePickerFilterStart = createDatePickerPanel(this.datePickerTripFilterStart,
                LocalizationProvider.getString("mainframe.datepicker.tooltip.from"));
        toolBarTrips.add(panelDatePickerFilterStart);
        
        this.datePickerTripFilterEnd = DateTimeHelper.createDatePicker();
        this.datePickerTripFilterEnd.setDate(LocalDate.of(LocalDate.now().getYear(), 12, 31));
        this.datePickerTripFilterEnd.addDateChangeListener(e -> processTripFilterInputChanged(false));
        var panelDatePickerFilterEnd = createDatePickerPanel(this.datePickerTripFilterEnd,
                LocalizationProvider.getString("mainframe.datepicker.tooltip.until"));
        toolBarTrips.add(panelDatePickerFilterEnd);
        
        JPanel panelTripsInfo = new JPanel();
        panelTripsRight.add(panelTripsInfo, BorderLayout.CENTER);
        panelTripsInfo.setLayout(null);
        
        this.tripBasicInfo = new TripBasicInfo();
        this.tripBasicInfo.setBounds(10, 11, 559, 298);
        this.tripBasicInfo.setEditable(false);
        panelTripsInfo.add(this.tripBasicInfo);
        
        /*** Trips ****/
        
        JPanel panelTripTypes = new JPanel();
        tabbedPane.addTab(null, IconFontSwing.buildIcon(FontAwesome.FOLDER_O, 15, Color.lightGray), panelTripTypes,
                LocalizationProvider.getString("mainframe.button.tooltip.triptypes"));
        panelTripTypes.setLayout(new BorderLayout(0, 0));
        
        JScrollPane scrollPaneTripTypes = new JScrollPane();
        panelTripTypes.add(scrollPaneTripTypes, BorderLayout.WEST);
        this.tableTripTypes = new JTable();
        this.tableTripTypes.setShowGrid(true);
        this.tableTripTypes.setColumnSelectionAllowed(false);
        this.tableTripTypes.setDragEnabled(false);
        this.tableTripTypes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.tableTripTypes.getTableHeader().setReorderingAllowed(false);
        this.tableTripTypes.getSelectionModel().addListSelectionListener(e -> processTableTripTypesSelectionChanged(e));
        scrollPaneTripTypes.setViewportView(this.tableTripTypes);
        scrollPaneTripTypes.setPreferredSize(new Dimension(400, 100));
        
        JPanel panelTripTypesRight = new JPanel();
        panelTripTypes.add(panelTripTypesRight, BorderLayout.CENTER);
        panelTripTypesRight.setLayout(new BorderLayout(0, 0));
        
        JToolBar toolBarTripTypes = new JToolBar();
        toolBarTripTypes.setFloatable(false);
        panelTripTypesRight.add(toolBarTripTypes, BorderLayout.NORTH);
        
        this.buttonNewTripType = new JButton(IconFontSwing.buildIcon(FontAwesome.PLUS_CIRCLE, 20, new Color(0, 150, 0)));
        this.buttonNewTripType.setToolTipText(LocalizationProvider.getString("mainframe.button.new"));
        this.buttonNewTripType.addActionListener(e -> openTripTypeDialog(null));
        toolBarTripTypes.add(this.buttonNewTripType);
        
        this.buttonEditTripType = new JButton(IconFontSwing.buildIcon(FontAwesome.PENCIL_SQUARE, 20, Color.ORANGE));
        this.buttonEditTripType.setToolTipText(LocalizationProvider.getString("mainframe.button.edit"));
        this.buttonEditTripType.addActionListener(e -> processTripTypeEditDeleteClick(true));
        toolBarTripTypes.add(this.buttonEditTripType);
        
        this.buttonDeleteTripType = new JButton(IconFontSwing.buildIcon(FontAwesome.MINUS_CIRCLE, 20, new Color(150, 0, 0)));
        this.buttonDeleteTripType.setToolTipText(LocalizationProvider.getString("mainframe.button.delete"));
        this.buttonDeleteTripType.addActionListener(e -> processTripTypeEditDeleteClick(false));
        toolBarTripTypes.add(this.buttonDeleteTripType);
        
        JPanel panelTripTypeInfo = new JPanel();
        panelTripTypesRight.add(panelTripTypeInfo, BorderLayout.CENTER);
        panelTripTypeInfo.setLayout(null);
        
        this.tripTypeBasicInfo = new TripTypeBasicInfo();
        this.tripTypeBasicInfo.setBounds(10, 11, 329, 37);
        this.tripTypeBasicInfo.setEditable(false);
        panelTripTypeInfo.add(this.tripTypeBasicInfo);
        
        /*** Reports ****/
        
        JPanel panelReport = new JPanel();
        tabbedPane.addTab(null, IconFontSwing.buildIcon(FontAwesome.FILE_TEXT_O, 15, Color.lightGray), panelReport,
                LocalizationProvider.getString("mainframe.button.tooltip.report"));
        panelReport.setLayout(null);
        
        this.reportPanel = new ReportPanel((message, title) -> showErrorMessage(message, title));
        this.reportPanel.setBounds(0, 0, 500, 343);
        this.reportPanel.setCreateReportAction(e -> performCreateReport(e));
        panelReport.add(this.reportPanel);
        
        /*** Common ****/

        this.statusBar = StatusBar.addStatusbar(this);
        this.statusBar.setStatusMessage(LocalizationProvider.getString("mainframe.statusbar.nodatabaseopen"));
        setActionButtonsEnabledState(false);
        setTripActionButtonsEnabledState(false);

        this.appConfiguration = AppConfiguration.loadAppConfiguration();
        String selectedTheme = null;
        if (this.appConfiguration != null) {
            selectedTheme = this.appConfiguration.SelectedTheme;
            if (!Strings.isNullOrEmpty(selectedTheme)) {
                setLookAndFeel(selectedTheme);
            }
            
            var reportConfiguration = this.appConfiguration.ReportConfig;
            if (reportConfiguration != null) {
                this.reportPanel.initialize(reportConfiguration);
            }
        }
        
        populateThemeMenuItems(this.mainMenuItemTheme, selectedTheme);
        reloadRecentDatabaseMenu();
        tabbedPane.setSelectedIndex(1);
    }
    
    private JPanel createDatePickerPanel(DatePicker datePicker, String caption) {
        final var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        final var label = new JLabel(caption);
        final var border = new EmptyBorder(0,0,0,5);
        label.setBorder(border);
        panel.add(label);
        panel.add(datePicker);
        panel.setMaximumSize(new Dimension(150, 25));
        datePicker.setBorder(border);
        return panel;
    }    
    
    private void openCustomerDialog(Customer entity) {
        var cutomerDialog = new CustomerDialog();
        cutomerDialog.showDialog(entity, this);
        reloadCustomersTable();
    }

    private void populateThemeMenuItems(JMenu mainMenuItem, String selectedTheme) {

        var availableThemes = new ThemeEntry[] {
                new ThemeEntry("Arc Dark", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkIJTheme"),
                new ThemeEntry("Arc Dark Contrast", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkContrastIJTheme"),
                new ThemeEntry("Light Flat", "com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme"),
                new ThemeEntry("Dark Flat", "com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme"),
                new ThemeEntry("GitHub", "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubIJTheme"),
                new ThemeEntry("GitHub Dark",
                        "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme"),
                new ThemeEntry("GitHub Dark Contrast",
                        "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkContrastIJTheme"),
                new ThemeEntry("Nimbus", NimbusLookAndFeel.class.getName()),
                new ThemeEntry("One Dark", "com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme"),
                new ThemeEntry("Spacegray", "com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme"),
                new ThemeEntry("Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel") };
        for (ThemeEntry themeEntry : availableThemes) {
            final var menuItem = new ThemeMenuItem(themeEntry.displayName, themeEntry.className);
            if (themeEntry.className.equals(selectedTheme)) {
                menuItem.setIcon(this.selectedThemeCheckIcon);;
            }
            menuItem.addActionListener(e -> processThemeMenuItemClicked(e));
            mainMenuItem.add(menuItem);
        }
    }
    
    private void processThemeMenuItemClicked(ActionEvent event) {
        try {
            final var eventSource = event.getSource();
            if (eventSource != null && eventSource instanceof ThemeMenuItem) {
                var selectedThemeMenuItem = (ThemeMenuItem) eventSource;
                var themeName = selectedThemeMenuItem.getThemeName();
                if (this.appConfiguration != null) {
                    this.appConfiguration.SelectedTheme = themeName;
                    saveConfiguration();
                }

                for (int i = 0; i < this.mainMenuItemTheme.getItemCount(); i++) {
                    var menuItem = this.mainMenuItemTheme.getItem(i);
                    if (menuItem != null && menuItem instanceof ThemeMenuItem) {
                        var themeMenuItem = (ThemeMenuItem)menuItem;
                        themeMenuItem.setIcon(null);
                    }
                }
                
                selectedThemeMenuItem.setIcon(this.selectedThemeCheckIcon);
                setLookAndFeel(themeName);
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }
    }

    private void setLookAndFeel(String className) {
        try {
            if (!Strings.isNullOrEmpty(className)) {
                UIManager.setLookAndFeel(className);
                FlatLaf.updateUI();
                EventQueue.invokeLater( () -> {
                    updateShowTableGrid();
                });
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }
    }
    
    private void updateShowTableGrid() {
        this.tableCustomers.setShowGrid(true);
        this.tableTrips.setShowGrid(true);
    }
    
    private void setActionButtonsEnabledState(boolean enabled) {
        this.buttonNewCustomer.setEnabled(enabled);
        this.buttonEditCustomer.setEnabled(enabled);
        this.buttonDeleteCustomer.setEnabled(enabled);
        this.buttonNewTripType.setEnabled(enabled);
        this.buttonEditTripType.setEnabled(enabled);
        this.buttonDeleteTripType.setEnabled(enabled);
        this.reportPanel.setEnabled(enabled);
    }
    
    private void setTripActionButtonsEnabledState(boolean enaled) {
        this.buttonNewTrip.setEnabled(enaled);
        this.buttonEditTrip.setEnabled(enaled);
        this.buttonDeleteTrip.setEnabled(enaled);
        this.toggleButtonTripFilterEnabled.setEnabled(enaled);
        this.datePickerTripFilterStart.setEnabled(enaled);
        this.datePickerTripFilterEnd.setEnabled(enaled);
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
                var databasePath = fileChooser.getSelectedFile().toString();
                if (!databasePath.endsWith(".db")) {
                    databasePath = databasePath + ".db";
                }
                
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
                reloadTripTypeTable();
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
        var tripTypes = TripManager.getTripTypes();
//        this.currentCustomers = createCustomersHashMap(customers);
        this.currentCustomers = createEntityIdHashMap(customers);
        this.currentTripTypes = createEntityIdHashMap(tripTypes);
        if (customers != null && tripTypes != null) {
            setTripActionButtonsEnabledState(customers.size() > 0);
            this.tripBasicInfo.reloadReferenceData(customers, tripTypes);
            this.tableCustomers.setModel(new EntityTableModel<Customer>(customers, this.customersColumnMap));
            var columnModel = this.tableCustomers.getColumnModel();
            if (columnModel != null && columnModel.getColumnCount() > 2) {
                var columnDistance = columnModel.getColumn(2);
                if (columnDistance != null) {
                    columnDistance.setMaxWidth(100);
                    columnDistance.setMinWidth(100);
                    columnDistance.setCellRenderer(RightTableCellRenderer);
                }
            }
        }
    }

    private void processTableCustomerSelectionChanged(ListSelectionEvent event) {
        if (event != null && event.getValueIsAdjusting()) {
            return;
        }

        var entity = getSelectedCustomerEntity();
        fillCustomerInfo(entity == null ? new Customer() : entity);
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

    private Customer getSelectedCustomerEntity() {
        return getSelectedEntity(this.tableCustomers, Customer.class);
    }

    private void fillCustomerInfo(Customer entity) {
        this.customerBasicInfo.fillFromEnity(entity);
    }

    private static HashMap<Integer, EntityDataModelHelper<Customer>> createCustomersColumnMap() {
        HashMap<Integer, EntityDataModelHelper<Customer>> columnMap = new HashMap<Integer, EntityDataModelHelper<Customer>>();
        columnMap.put(0, new EntityDataModelHelper<Customer>(
                LocalizationProvider.getString("customerdialog.label.name"), (entity) -> entity.getName()));
        columnMap.put(1, new EntityDataModelHelper<Customer>(
                LocalizationProvider.getString("customerdialog.label.city"), (entity) -> entity.getCity()));
        columnMap.put(2, new EntityDataModelHelper<Customer>(
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
        showMessage(message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
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
    
    private HashMap<Integer, EntityDataModelHelper<Trip>> createTripsColumnMap() {
        HashMap<Integer, EntityDataModelHelper<Trip>> columnMap = new HashMap<Integer, EntityDataModelHelper<Trip>>();
        columnMap.put(0, new EntityDataModelHelper<Trip>(
                LocalizationProvider.getString("tripbasicinfo.label.date"), (entity) -> DateTimeHelper.toDisplayDateFormat(entity.getLocalDate())));
        columnMap.put(1, new EntityDataModelHelper<Trip>(
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
    
    private void openTripDialog(Trip entity) {
        if (this.currentCustomers != null && this.currentTripTypes != null) {        
            var tripDialog = new TripDialog(this.currentCustomers.values(), this.currentTripTypes.values());
            tripDialog.showDialog(entity, this);
            reloadTripTable();
        }
    }
   
    private void reloadTripTable() {
        if (!DatabaseProvider.getIsOpen()) {
            return;
        }
        
        List<Trip> trips;
        if (this.toggleButtonTripFilterEnabled.isSelected() && 
                this.datePickerTripFilterStart.isTextFieldValid() &&
                this.datePickerTripFilterEnd.isTextFieldValid()) {            
            trips = TripManager.getTrips(this.datePickerTripFilterStart.getDate(), this.datePickerTripFilterEnd.getDate());            
        } else {
            trips = TripManager.getTrips();
        }

        this.tableTrips.setModel(new EntityTableModel<Trip>(trips, this.tripsColumnMap));
        var columnModel = this.tableTrips.getColumnModel();
        if (columnModel != null && columnModel.getColumnCount() > 0) {
            var columnDate = columnModel.getColumn(0);
            if (columnDate != null) {
                columnDate.setMaxWidth(80);
                columnDate.setMinWidth(80);
            }
        }
    }
    
    private void processTableTripsSelectionChanged(ListSelectionEvent event) {
        if (event != null && event.getValueIsAdjusting()) {
            return;
        }

        var entity = getSelectedTripEntity();
        this.tripBasicInfo.fillFromEnity((entity == null ? new Trip() : entity));
    }
    
    private Trip getSelectedTripEntity() {
        return getSelectedEntity(this.tableTrips, Trip.class);
    }
    
    private static <T> T getSelectedEntity(JTable table, Class<? extends T> type) {
        T entity = null;
        if (table != null) {
            var selectedRowIndex = table.getSelectedRow();
            if (selectedRowIndex >= 0) {
                var currentModel = table.getModel();
                if (currentModel != null && currentModel instanceof EntityTableModel) {
                    var entityTableModel = EntityTableModel.class.cast(currentModel);
                    if (entityTableModel != null) {
                        var value = entityTableModel.getEntity(selectedRowIndex);
                        if (value != null) {
                            entity = type.cast(value);
                        }
                    }
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
                        DateTimeHelper.toDisplayDateFormat(entity.getLocalDate()));
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
    
    private void processTripFilterInputChanged(boolean toggleButtonChanged) {
        boolean reload = toggleButtonChanged || this.toggleButtonTripFilterEnabled.isSelected();
        if (reload) {
            reloadTripTable();
        }
    }
    
    private void openTripTypeDialog(TripType entity) {
        var dialog = new TripTypeDialog();
        dialog.showDialog(entity, this);
        reloadTripTypeTable();
    }
   
    private void reloadTripTypeTable() {
        if (!DatabaseProvider.getIsOpen()) {
            return;
        }

        var tripTypes = TripManager.getTripTypes();
        this.tableTripTypes.setModel(new EntityTableModel<TripType>(tripTypes, this.tripTypesColumnMap));
    }
    
    private void processTableTripTypesSelectionChanged(ListSelectionEvent event) {
        if (event != null && event.getValueIsAdjusting()) {
            return;
        }

        var entity = getSelectedTripType();
        this.tripTypeBasicInfo.fillFromEnity((entity == null ? new TripType() : entity));
    }
    
    private TripType getSelectedTripType() {
        return getSelectedEntity(this.tableTripTypes, TripType.class);
    }
    
    private void processTripTypeEditDeleteClick(boolean edit) {
        var entity = getSelectedTripType();
        var title = edit ? LocalizationProvider.getString("mainframe.button.edit")
                : LocalizationProvider.getString("mainframe.button.delete");
        if (entity != null && entity.getId() > 0) {
            if (edit) {
                openTripTypeDialog(entity);
            } else {
                var message = String.format(LocalizationProvider.getString("mainframe.message.confirmtriptypedelete"), entity.getName());
                if (askForConfirmation(message, LocalizationProvider.getString("mainframe.button.delete"))) {
                    if (!TripManager.checkIsTripTypeAssigned(entity.getId())) {
                        entity.setDeleted();
                        DatabaseProvider.updateEntity(entity);
                        reloadTripTypeTable();
                    } else {
                        showErrorMessage(LocalizationProvider.getString("triptypedialog.message.deletenotpossiblestillinuse"), title);
                    }                    
                }
            }
        } else {
            showErrorMessage(LocalizationProvider.getString("mainframe.message.notriptypeselected"), title);
        }
    }
    
    private boolean createReportAsync(ReportConfiguration configuration) {
        boolean result = false;
        if (configuration == null) {
            return result;
        }
        
        try {
            var outputDirectory = configuration.OutputDirectory;
            var outputFileName = configuration.OutputFileName;
            if (!Strings.isNullOrEmpty(outputDirectory) && !Strings.isNullOrEmpty(outputFileName)) {
                outputFileName = outputFileName + "_" + DateTimeHelper.toSortableDate(LocalDate.now());
                
                var generator = new ReportGenerator(this.currentCustomers, this.currentTripTypes);
                boolean success = true;
                if (configuration.GenerateSimpleYears) {
                    var fileName = outputFileName + "_" + LocalizationProvider.getString("report.filename.suffix.simpleyears") + ".pdf";
                    var outputPath = Paths.get(outputDirectory, fileName);
                    success = generator.generateReport(ReportGenerator.REPORT_SIMPLE_YEARS, configuration.YearToReport, outputPath); 
                }
                
                if (success && configuration.GenerateSimpleMonths) {
                    var detailedFileName = outputFileName + "_" + LocalizationProvider.getString("report.filename.suffix.simplemonths") + ".pdf";
                    var outputPath = Paths.get(outputDirectory, detailedFileName);
                    success = generator.generateReport(ReportGenerator.REPORT_SIMPLE_MONTHS, configuration.YearToReport, outputPath); 
                }
                
                if (success && configuration.GenerateDetailed) {
                    var detailedFileName = outputFileName + "_" + LocalizationProvider.getString("report.filename.suffix.detailed") + ".pdf";
                    var outputPath = Paths.get(outputDirectory, detailedFileName);
                    success = generator.generateReport(ReportGenerator.REPORT_DETAILED, configuration.YearToReport, outputPath); 
                }
                
                result = success;
            }
            
        } catch (Exception ex) {
            Logger.Log(ex);
        } 
        
        return result;
    }
    
    private void performCreateReport(ReportConfiguration configuration) {
        if (configuration == null) {
            return;
        }
        
        try {
            var currentAppConfiguration = this.appConfiguration;
            if (currentAppConfiguration == null) {
                currentAppConfiguration = this.appConfiguration = new AppConfiguration();
            }
            
            currentAppConfiguration.ReportConfig = configuration;
            currentAppConfiguration.saveConfiguration();
            var waitDialog = new WaitDialog();
            var worker = new SwingWorker<Boolean, Void>() {
                @Override
                public Boolean doInBackground() {
                    return createReportAsync(configuration);
                }

                @Override
                public void done() {
                    try {
                        if (get()) {
                            showMessage(LocalizationProvider.getString("report.message.createsuccess"), 
                                    LocalizationProvider.getString("mainframe.button.tooltip.report"), JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            showErrorMessage(LocalizationProvider.getString("report.message.createfailed"),
                                    LocalizationProvider.getString("mainframe.button.tooltip.report"));
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
        } catch (Exception ex) {
            Logger.Log(ex);
        }
    }
    
    private HashMap<Integer, EntityDataModelHelper<TripType>> createTripTypesColumnMap() {
        HashMap<Integer, EntityDataModelHelper<TripType>> columnMap = new HashMap<Integer, EntityDataModelHelper<TripType>>();
        columnMap.put(0, new EntityDataModelHelper<TripType>(
                LocalizationProvider.getString("triptypebasicinfo.label.name"), (entity) -> entity.getName()));
        return columnMap;
    }
        
    private <T extends IEntityId> HashMap<Integer, T> createEntityIdHashMap(List<T> entities) {
        if (entities != null) {
            var hashMap = new HashMap<Integer, T>(entities.size());
            for (var entity : entities) {
                if (entity != null) {
                    hashMap.put(entity.getId(), entity);
                }
            }
            
            return hashMap;
        }
        
        return new HashMap<Integer, T>(0);
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
