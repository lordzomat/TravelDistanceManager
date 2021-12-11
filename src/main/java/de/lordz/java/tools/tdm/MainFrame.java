package de.lordz.java.tools.tdm;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.google.common.base.Strings;
import de.lordz.java.tools.tdm.common.AppConstants;
import de.lordz.java.tools.tdm.common.DateTimeHelper;
import de.lordz.java.tools.tdm.common.IUserNotificationHandler;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.common.Logger;
import de.lordz.java.tools.tdm.config.AppConfiguration;
import de.lordz.java.tools.tdm.config.ReportConfiguration;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import javax.swing.JTabbedPane;
import javax.swing.Icon;

public class MainFrame extends JFrame implements IUserNotificationHandler {

    private static final long serialVersionUID = 404497382974994431L;
    private final Icon selectedThemeCheckIcon;
    private AppConfiguration appConfiguration;
    private JPanel contentPane;
    private JMenu mainMenuItemTheme;
    private StatusBar statusBar;
    private JMenu fileMenuRecentDatabases;
    private CustomerPanel customerPanel;
    private TripPanel tripPanel;
    private TripTypePanel tripTypePanel;
    private TravelAllowancePanel travelAllowancePanel;
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
        
        /*** Entity panels ****/
        this.customerPanel = new CustomerPanel(this);
        this.customerPanel.setTableReloadedActionListener((e -> customerOrTripTypesReloadedEventHandler(e)));
        this.tripTypePanel = new TripTypePanel(this);
        this.tripTypePanel.setTableReloadedActionListener((e -> customerOrTripTypesReloadedEventHandler(e)));
        this.tripPanel = new TripPanel(this, this.customerPanel, this.tripTypePanel);        

        /*** Customers ****/        
        
        tabbedPane.addTab(null, IconFontSwing.buildIcon(FontAwesome.USERS, 15, Color.lightGray), this.customerPanel,
                LocalizationProvider.getString("mainframe.button.tooltip.customers"));
        
        /*** Trips ****/
        
        tabbedPane.addTab(null, IconFontSwing.buildIcon(FontAwesome.CAR, 15, Color.lightGray), this.tripPanel,
                LocalizationProvider.getString("mainframe.button.tooltip.trips"));
                
        /*** Trips ****/
        
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0 };
        gridBagLayout.rowHeights = new int[] {0, 0};
        gridBagLayout.columnWeights = new double[]{ 1.0 };
        gridBagLayout.rowWeights = new double[]{0.5, 0.5};
        var panelGeneralData = new JPanel(gridBagLayout);        
        tabbedPane.addTab(null, IconFontSwing.buildIcon(FontAwesome.COG, 15, Color.lightGray), panelGeneralData,
                LocalizationProvider.getString("mainframe.button.tooltip.commondata"));
        this.travelAllowancePanel = new TravelAllowancePanel(this);
        this.travelAllowancePanel.setBorder(new TitledBorder(null, LocalizationProvider.getString("mainframe.panel.title.travelallowance"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
        var constraintTripTypes = new GridBagConstraints();
        constraintTripTypes.anchor = GridBagConstraints.WEST;
        constraintTripTypes.fill = GridBagConstraints.BOTH;
        constraintTripTypes.insets = new Insets(0, 0, 5, 5);
        constraintTripTypes.gridx = 0;
        constraintTripTypes.gridy = 0;
        var constraintTravelAllowances = new GridBagConstraints();
        constraintTravelAllowances.anchor = GridBagConstraints.WEST;
        constraintTravelAllowances.fill = GridBagConstraints.BOTH;
        constraintTravelAllowances.insets = new Insets(0, 0, 5, 5);
        constraintTravelAllowances.gridx = 0;
        constraintTravelAllowances.gridy = 1;
        this.tripTypePanel.setBorder(new TitledBorder(null, LocalizationProvider.getString("mainframe.panel.title.triptypes"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panelGeneralData.add(this.tripTypePanel, constraintTripTypes);
        panelGeneralData.add(this.travelAllowancePanel, constraintTravelAllowances);
        
        /*** Reports ****/
        
        JPanel panelReport = new JPanel();
        tabbedPane.addTab(null, IconFontSwing.buildIcon(FontAwesome.FILE_TEXT_O, 15, Color.lightGray), panelReport,
                LocalizationProvider.getString("mainframe.button.tooltip.report"));
        panelReport.setLayout(new BorderLayout(0, 0));
        var panelReportContent = new JPanel(new BorderLayout(0, 0));
        panelReport.add(panelReportContent, BorderLayout.WEST);
        this.reportPanel = new ReportPanel((message, title) -> showErrorMessage(message, title));
        this.reportPanel.setBounds(0, 0, 500, 343);
        this.reportPanel.setCreateReportAction(e -> performCreateReport(e));
        panelReportContent.add(this.reportPanel, BorderLayout.NORTH);
        
        /*** Common ****/

        this.statusBar = StatusBar.addStatusbar(this);
        this.statusBar.setStatusMessage(LocalizationProvider.getString("mainframe.statusbar.nodatabaseopen"));
        setActionButtonsEnabledState(false);

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
    
    public void showErrorMessage(String message) { 
        showErrorMessage(message, AppConstants.ApplicationName);
    }
    
    public void showErrorMessage(Component component, String message) { 
        showErrorMessage(component, message, AppConstants.ApplicationName);
    }
    
    public void showErrorMessage(String message, String title) {
        showMessage(message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public void showErrorMessage(Component component, String message, String title) {
        showMessage(component, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public void showMessage(String message, String title, int messageType) {
        showMessage(null, message, title, messageType);
    }
    
    public void showMessage(Component component, String message, String title, int messageType) {
        JOptionPane.showMessageDialog(component != null ? component : this, message, title, messageType);
    }
    
    public boolean askForConfirmation(String message, String title) {
        return askForConfirmation(null, message, title);
    }
    
    public boolean askForConfirmation(Component component, String message, String title) {
        return JOptionPane.showConfirmDialog(component != null ? component : this, message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
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
        this.customerPanel.setShowGrid(true);
        this.tripPanel.setShowGrid(true);
        this.tripTypePanel.setShowGrid(true);
        this.travelAllowancePanel.setShowGrid(true);
    }
    
    private void setActionButtonsEnabledState(boolean enabled) {
        this.customerPanel.setEnabled(enabled);
        this.tripPanel.setEnabled(enabled);
        this.tripTypePanel.setEnabled(enabled);
        this.travelAllowancePanel.setEnabled(enabled);
        this.reportPanel.setEnabled(enabled);
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
                this.customerPanel.reloadTable();
                this.tripPanel.reloadTable();
                this.tripTypePanel.reloadTable();
                this.travelAllowancePanel.reloadTable();
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }
    }

    private void saveConfiguration() {
        if (!this.appConfiguration.saveConfiguration()) {
            showErrorMessage(String.format(LocalizationProvider.getString("mainframe.error.saveconfiguration"),
                    AppConfiguration.ConfigurationFilePath));
        }
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

    private static void setLocale(String language, String country) {
        LocalizationProvider.setLocale(language, country);
    }
       
    private void customerOrTripTypesReloadedEventHandler(ActionEvent event) {
        this.tripPanel.reloadReferenceData();
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
                
                var generator = new ReportGenerator(this.customerPanel.getCachedEntities(), this.tripTypePanel.getCachedEntities(),
                        this.travelAllowancePanel.getCachedEntities());
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

    private class ThemeEntry {
        public final String displayName;
        public final String className;

        public ThemeEntry(String displayName, String className) {
            this.displayName = displayName;
            this.className = className;
        }
    }
}
