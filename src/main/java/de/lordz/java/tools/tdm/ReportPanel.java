package de.lordz.java.tools.tdm;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.border.TitledBorder;

import com.google.common.base.Strings;

import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.config.ReportConfiguration;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;

public class ReportPanel extends JPanel {

    private static final long serialVersionUID = 1456409790461333696L;
    private JTextField textFieldOutputDirectory;
    private JTextField textFieldOutputFileName;
    private JTextField textFieldYear;
    private JCheckBox checkboxSimpleYear;
    private JCheckBox checkBoxSimpleMonths;
    private JCheckBox checkBoxDetailed;
    private JButton buttonStart;
    private ReportConfiguration currentConfiguration;
    private Consumer<ReportConfiguration> createReportAction;
    private BiConsumer<String, String> showErrorMessageConsumer;
    
    public ReportPanel(BiConsumer<String, String> showErrorMessageConsumer) {
        setLayout(new BorderLayout(0, 0));
        this.showErrorMessageConsumer = showErrorMessageConsumer;
        var panelOutputSettings = new JPanel();
        panelOutputSettings.setBorder(new TitledBorder(null, LocalizationProvider.getString("reportpanel.title.outputsettings"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(panelOutputSettings, BorderLayout.NORTH);
        var gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {100, 300, 0};
        gridBagLayout.rowHeights = new int[] {30, 30, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        panelOutputSettings.setLayout(gridBagLayout);
        
        var labelOutputDirectory = new JLabel(LocalizationProvider.getString("reportpanel.label.outputdirectory"));
        var constraintlabelOutputDirectory = new GridBagConstraints();
        constraintlabelOutputDirectory.anchor = GridBagConstraints.WEST;
        constraintlabelOutputDirectory.insets = new Insets(0, 0, 5, 5);
        constraintlabelOutputDirectory.gridx = 0;
        constraintlabelOutputDirectory.gridy = 0;
        panelOutputSettings.add(labelOutputDirectory, constraintlabelOutputDirectory);
        
        this.textFieldOutputDirectory = new JTextField();
        var constrainttextFieldOutputDirectory = new GridBagConstraints();
        constrainttextFieldOutputDirectory.insets = new Insets(0, 0, 5, 0);
        constrainttextFieldOutputDirectory.fill = GridBagConstraints.HORIZONTAL;
        constrainttextFieldOutputDirectory.gridx = 1;
        constrainttextFieldOutputDirectory.gridy = 0;
        panelOutputSettings.add(this.textFieldOutputDirectory, constrainttextFieldOutputDirectory);
        this.textFieldOutputDirectory.setColumns(10);
        
        var labelOutputFileName = new JLabel(LocalizationProvider.getString("reportpanel.label.outputfilename"));
        var constraintLabelOutputFileName = new GridBagConstraints();
        constraintLabelOutputFileName.anchor = GridBagConstraints.WEST;
        constraintLabelOutputFileName.insets = new Insets(0, 0, 0, 5);
        constraintLabelOutputFileName.gridx = 0;
        constraintLabelOutputFileName.gridy = 1;
        panelOutputSettings.add(labelOutputFileName, constraintLabelOutputFileName);
        
        this.textFieldOutputFileName = new JTextField();
        this.textFieldOutputFileName.setText("");
        var constraintTextFieldOutputFileName = new GridBagConstraints();
        constraintTextFieldOutputFileName.fill = GridBagConstraints.HORIZONTAL;
        constraintTextFieldOutputFileName.gridx = 1;
        constraintTextFieldOutputFileName.gridy = 1;
        panelOutputSettings.add(this.textFieldOutputFileName, constraintTextFieldOutputFileName);
        this.textFieldOutputFileName.setColumns(10);
        
        var panelReport = new JPanel();
        panelReport.setBorder(new TitledBorder(null, LocalizationProvider.getString("reportpanel.title.reportsettings"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(panelReport, BorderLayout.CENTER);
        var gridBagLayoutPanelReport = new GridBagLayout();
        gridBagLayoutPanelReport.columnWidths = new int[] {0};
        gridBagLayoutPanelReport.rowHeights = new int[] {40, 0};
        gridBagLayoutPanelReport.columnWeights = new double[]{1.0};
        gridBagLayoutPanelReport.rowWeights = new double[]{0.0, 1.0};
        panelReport.setLayout(gridBagLayoutPanelReport);
        
        var panelDateSelection = new JPanel();
        panelDateSelection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        var gridBagConstraintPanelDateSelection = new GridBagConstraints();
        gridBagConstraintPanelDateSelection.insets = new Insets(0, 0, 5, 5);
        gridBagConstraintPanelDateSelection.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraintPanelDateSelection.gridx = 0;
        gridBagConstraintPanelDateSelection.gridy = 0;
        panelReport.add(panelDateSelection, gridBagConstraintPanelDateSelection);
        var gridBagLayoutPanelDateSelection = new GridBagLayout();
        gridBagLayoutPanelDateSelection.columnWidths = new int[] {0, 0, 0, 0};
        gridBagLayoutPanelDateSelection.rowHeights = new int[] {0};
        gridBagLayoutPanelDateSelection.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0};
        gridBagLayoutPanelDateSelection.rowWeights = new double[]{0.0};
        panelDateSelection.setLayout(gridBagLayoutPanelDateSelection);
        
        var labelYear = new JLabel(LocalizationProvider.getString("reportpanel.label.reportyear"));
        var constraintLabelYear = new GridBagConstraints();
        constraintLabelYear.insets = new Insets(0, 0, 0, 5);
        constraintLabelYear.anchor = GridBagConstraints.EAST;
        constraintLabelYear.gridx = 0;
        constraintLabelYear.gridy = 0;
        panelDateSelection.add(labelYear, constraintLabelYear);
        
        this.textFieldYear = new JTextField();
        var constraintTextFieldYear = new GridBagConstraints();
        constraintTextFieldYear.insets = new Insets(0, 0, 0, 5);
        constraintTextFieldYear.fill = GridBagConstraints.HORIZONTAL;
        constraintTextFieldYear.gridx = 1;
        constraintTextFieldYear.gridy = 0;
        panelDateSelection.add(this.textFieldYear, constraintTextFieldYear);
        this.textFieldYear.setColumns(10);
        
        var panelTypeSelection = new JPanel();
        var constraintPanelTypeSelection = new GridBagConstraints();
        constraintPanelTypeSelection.anchor = GridBagConstraints.NORTHWEST;
        constraintPanelTypeSelection.insets = new Insets(0, 0, 0, 5);
        constraintPanelTypeSelection.gridx = 0;
        constraintPanelTypeSelection.gridy = 1;
        panelReport.add(panelTypeSelection, constraintPanelTypeSelection);
        var gridBagLayoutPanelTypeSelection = new GridBagLayout();
        gridBagLayoutPanelTypeSelection.columnWidths = new int[] {0, 300};
        gridBagLayoutPanelTypeSelection.rowHeights = new int[] {0, 0, 0, 0, 0};
        gridBagLayoutPanelTypeSelection.columnWeights = new double[]{0.0};
        gridBagLayoutPanelTypeSelection.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
        panelTypeSelection.setLayout(gridBagLayoutPanelTypeSelection);
        
        var labelReportKinds = new JLabel(LocalizationProvider.getString("reportpanel.label.reportkind"));
        var constraintLabelReportTypes = new GridBagConstraints();
        constraintLabelReportTypes.anchor = GridBagConstraints.WEST;
        constraintLabelReportTypes.insets = new Insets(0, 0, 5, 0);
        constraintLabelReportTypes.gridx = 0;
        constraintLabelReportTypes.gridy = 0;
        panelTypeSelection.add(labelReportKinds, constraintLabelReportTypes);
        
        this.checkboxSimpleYear = new JCheckBox(LocalizationProvider.getString("reportpanel.checkbox.simpleyears"));
        var constraintCheckboxSimpleYear = new GridBagConstraints();
        constraintCheckboxSimpleYear.anchor = GridBagConstraints.WEST;
        constraintCheckboxSimpleYear.insets = new Insets(0, 0, 5, 0);
        constraintCheckboxSimpleYear.gridx = 0;
        constraintCheckboxSimpleYear.gridy = 1;
        panelTypeSelection.add(this.checkboxSimpleYear, constraintCheckboxSimpleYear);
        
        this.checkBoxSimpleMonths = new JCheckBox(LocalizationProvider.getString("reportpanel.checkbox.simplemonths"));
        var constraintCheckBoxSimpleMonths = new GridBagConstraints();
        constraintCheckBoxSimpleMonths.anchor = GridBagConstraints.WEST;
        constraintCheckBoxSimpleMonths.insets = new Insets(0, 0, 5, 0);
        constraintCheckBoxSimpleMonths.gridx = 0;
        constraintCheckBoxSimpleMonths.gridy = 2;
        panelTypeSelection.add(this.checkBoxSimpleMonths, constraintCheckBoxSimpleMonths);
        
        this.checkBoxDetailed = new JCheckBox(LocalizationProvider.getString("reportpanel.checkbox.detailed"));
        var constraintCheckBoxDetailed = new GridBagConstraints();
        constraintCheckBoxDetailed.anchor = GridBagConstraints.WEST;
        constraintCheckBoxDetailed.insets = new Insets(0, 0, 5, 0);
        constraintCheckBoxDetailed.gridx = 0;
        constraintCheckBoxDetailed.gridy = 3;
        panelTypeSelection.add(this.checkBoxDetailed, constraintCheckBoxDetailed);
        
        this.buttonStart = new JButton(LocalizationProvider.getString("reportpanel.button.create"));
        this.buttonStart.addActionListener(e -> createReports());
        var gridBagbuttonStart = new GridBagConstraints();
        gridBagbuttonStart.anchor = GridBagConstraints.WEST;
        gridBagbuttonStart.gridx = 0;
        gridBagbuttonStart.gridy = 4;
        panelTypeSelection.add(this.buttonStart, gridBagbuttonStart);
        
        this.textFieldYear.setText(String.valueOf(LocalDate.now().getYear()));
    }
    
    public void initialize(ReportConfiguration configuration) {
        if (configuration == null) {
            configuration = new ReportConfiguration();
        }
        
        this.currentConfiguration = configuration;
        this.textFieldOutputDirectory.setText(configuration.OutputDirectory);
        this.textFieldOutputFileName.setText(configuration.OutputFileName);
        int yearToReport = configuration.YearToReport;
        if (yearToReport > 2000) {
            this.textFieldYear.setText(Integer.toString(yearToReport));
        }
        
        this.checkboxSimpleYear.setSelected(configuration.GenerateSimpleYears);
        this.checkBoxSimpleMonths.setSelected(configuration.GenerateSimpleMonths);
        this.checkBoxDetailed.setSelected(configuration.GenerateDetailed);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        this.textFieldOutputDirectory.setEnabled(enabled);
        this.textFieldOutputFileName.setEnabled(enabled);
        this.textFieldYear.setEnabled(enabled);
        this.checkboxSimpleYear.setEnabled(enabled);
        this.checkBoxSimpleMonths.setEnabled(enabled);
        this.checkBoxDetailed.setEnabled(enabled);
        this.buttonStart.setEnabled(enabled);
    }
    
    public void setCreateReportAction(Consumer<ReportConfiguration> consumer) {
        this.createReportAction = consumer;
    }
    
    private void createReports() {
        if (this.createReportAction != null) {
            var configuration = this.currentConfiguration != null ? this.currentConfiguration : new ReportConfiguration();
            configuration.OutputDirectory = this.textFieldOutputDirectory.getText();
            configuration.OutputFileName = this.textFieldOutputFileName.getText();
            var yearToReport = this.textFieldYear.getText();
            if (!Strings.isNullOrEmpty(yearToReport) && isValidYear(yearToReport)) {
                configuration.YearToReport = Integer.parseInt(yearToReport);
                configuration.GenerateSimpleYears = this.checkboxSimpleYear.isSelected();
                configuration.GenerateSimpleMonths = this.checkBoxSimpleMonths.isSelected();
                configuration.GenerateDetailed = this.checkBoxDetailed.isSelected();
                this.createReportAction.accept(configuration);
            } else {
                if (this.showErrorMessageConsumer != null) {
                    this.showErrorMessageConsumer.accept(LocalizationProvider.getString("report.message.invalidyear"),
                            LocalizationProvider.getString("mainframe.button.tooltip.report"));
                }
            }
        }
    }
    
    private static boolean isValidYear(String value) {
        return Pattern.matches("^[0-9]{4}$", value);
    }
}
