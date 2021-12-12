package de.lordz.java.tools.tdm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;

import com.github.lgooddatepicker.components.DatePicker;
import com.google.common.base.Strings;

import de.lordz.java.tools.tdm.common.DateTimeHelper;
import de.lordz.java.tools.tdm.common.IUserNotificationHandler;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.common.Logger;
import de.lordz.java.tools.tdm.entities.Trip;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

/**
 * Panel to manage trips.
 * 
 * @author lordzomat
 *
 */
public class TripPanel extends EntityModelPanelBase<Trip> {

    private static final long serialVersionUID = 5559376073959199333L;
    private final HashMap<Integer, EntityDataModelHelper<Trip>> columnMap = createColumnMap();
    private final TripDataPanel tripDataPanel;
    private final Component mainWindow;  
    private final CustomerPanel customerPanel;
    private final TripTypePanel tripTypePanel;
    private final IUserNotificationHandler userNotificationHandler;
    private final TripDialog dialog;
    private final JToggleButton toggleButtonTripFilterEnabled;
    private final JComboBox<FilterType> comboBoxFilterType;
    private final JPanel panelDatePickerFilterStart;
    private final JPanel panelDatePickerFilterEnd;
    private final DatePicker datePickerTripFilterStart;
    private final DatePicker datePickerTripFilterEnd;
    private final JPanel panelFilterMonth;
    private final JPanel panelFilterMonthYear;
    private final JComboBox<FilterMonth> comboBoxFilterMonth;
    private final JButton buttonFilterGoToPreviousMonth;
    private final JButton buttonFilterGoToNextMonth;
    private final JTextField textFieldFilterYear;
    
    /**
     * Initializes a new instance of the <CODE>TripPanel</CODE> class.
     * 
     * @param mainWindow The main window component.
     */
    public TripPanel(Component mainWindow, CustomerPanel customerPanel, TripTypePanel tripTypePanel) {
        super(Trip.class);
        this.userNotificationHandler = (IUserNotificationHandler)mainWindow;
        if (this.userNotificationHandler == null) {
            throw new IllegalArgumentException("Main window does not implement user notification handler!");
        }
        
        this.mainWindow = mainWindow;
        this.customerPanel = customerPanel;
        this.tripTypePanel = tripTypePanel;
        this.dialog = new TripDialog(this.userNotificationHandler);
        this.tripDataPanel = new TripDataPanel();
        this.tripDataPanel.setEditable(false);        
        this.toggleButtonTripFilterEnabled = new JToggleButton(IconFontSwing.buildIcon(FontAwesome.FILTER, 20, new Color(0, 145, 255)));
        this.toggleButtonTripFilterEnabled.setSelected(true);
        this.toggleButtonTripFilterEnabled.setToolTipText(LocalizationProvider.getString("trippanel.button.tooltip.tripFilter"));
        this.toggleButtonTripFilterEnabled.addItemListener(e -> processFilterInputChanged(true));
        addToolbarComponent(this.toggleButtonTripFilterEnabled);
        
        this.comboBoxFilterType = new JComboBox<FilterType>();
        this.comboBoxFilterType.addItem(FilterType.YEAR_FILER);
        this.comboBoxFilterType.addItem(FilterType.MONTH_FILTER);
        this.comboBoxFilterType.addItem(FilterType.RANGE_FILTER);
        this.comboBoxFilterType.addActionListener(e -> performFilterTypeChanged());
//        this.comboBoxFilterType.setPrototypeDisplayValue(FilterType.RANGE_FILTER);
        var panelFilterType = new JPanel(new BorderLayout(0, 0));
        panelFilterType.setMaximumSize(new Dimension(120, 25));
        panelFilterType.add(this.comboBoxFilterType, BorderLayout.WEST);
        addToolbarComponent(panelFilterType);
        
        var currentYear = LocalDate.now().getYear();
        this.datePickerTripFilterStart = DateTimeHelper.createDatePicker();
        this.datePickerTripFilterStart.setDate(LocalDate.of(currentYear, 1, 1));
        this.datePickerTripFilterStart.addDateChangeListener(e -> processFilterInputChanged(false));
        this.panelDatePickerFilterStart = createFilterPanel(this.datePickerTripFilterStart,
                LocalizationProvider.getString("trippanel.label.filter.from"), 150);
        addToolbarComponent(this.panelDatePickerFilterStart);
        
        this.datePickerTripFilterEnd = DateTimeHelper.createDatePicker();
        this.datePickerTripFilterEnd.setDate(LocalDate.of(currentYear, 12, 31));
        this.datePickerTripFilterEnd.addDateChangeListener(e -> processFilterInputChanged(false));
        this.panelDatePickerFilterEnd = createFilterPanel(this.datePickerTripFilterEnd,
                LocalizationProvider.getString("trippanel.label.filter.until"), 150);
        addToolbarComponent(this.panelDatePickerFilterEnd);
        
        this.comboBoxFilterMonth = new JComboBox<TripPanel.FilterMonth>();
        this.comboBoxFilterMonth.addActionListener(e -> processFilterInputChanged(false));
        var filterMonths = FilterMonth.MONTHS;
        for (var month : filterMonths) {
            this.comboBoxFilterMonth.addItem(month);
        }
        this.panelFilterMonth = createFilterPanel(this.comboBoxFilterMonth, LocalizationProvider.getString("trippanel.label.filter.month"), 150);
        addToolbarComponent(this.panelFilterMonth);
        
        this.buttonFilterGoToPreviousMonth = new JButton(IconFontSwing.buildIcon(FontAwesome.ANGLE_LEFT, 20, new Color(0, 145, 255)));
        this.buttonFilterGoToPreviousMonth.setToolTipText(LocalizationProvider.getString("trippanel.label.filter.previousmonth"));
        this.buttonFilterGoToPreviousMonth.addActionListener(e -> performMoveMonth(false));
        addToolbarComponent(this.buttonFilterGoToPreviousMonth);
        this.buttonFilterGoToNextMonth = new JButton(IconFontSwing.buildIcon(FontAwesome.ANGLE_RIGHT, 20, new Color(0, 145, 255)));
        this.buttonFilterGoToNextMonth.setToolTipText(LocalizationProvider.getString("trippanel.label.filter.nextmonth"));
        this.buttonFilterGoToNextMonth.addActionListener(e -> performMoveMonth(true));
        addToolbarComponent(this.buttonFilterGoToNextMonth);
        
        this.textFieldFilterYear = new JTextField(String.valueOf(currentYear));
        this.textFieldFilterYear.setHorizontalAlignment(JTextField.CENTER);
        this.textFieldFilterYear.setColumns(5);
        this.textFieldFilterYear.addActionListener(e -> processYearFilterInputChanged());
        this.textFieldFilterYear.getDocument()
                .addDocumentListener(new EntityTextChangeDocumentListener(e -> processYearFilterInputChanged()));
        this.panelFilterMonthYear = createFilterPanel(this.textFieldFilterYear, LocalizationProvider.getString("trippanel.label.filter.year"), 90);
        addToolbarComponent(this.panelFilterMonthYear);
        
        setContentComponent(this.tripDataPanel);
        performFilterTypeChanged();
    }
    
    public void reloadReferenceData() {
        var cachedCustomers = this.customerPanel.getCachedEntities();
        var cachedTripTypes = this.tripTypePanel.getCachedEntities();
        if (cachedCustomers != null && cachedTripTypes != null) {
            this.tripDataPanel.reloadReferenceData(cachedCustomers.values(), cachedTripTypes.values());
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.toggleButtonTripFilterEnabled.setEnabled(enabled);
        this.comboBoxFilterType.setEnabled(enabled);
        this.textFieldFilterYear.setEnabled(enabled);
        this.datePickerTripFilterStart.setEnabled(enabled);
        this.datePickerTripFilterEnd.setEnabled(enabled);
    }
    
    @Override
    protected void performActionNew() {
        openDialog(null);
    }

    @Override
    protected void performEditDeleteItem(boolean edit) {
        var entity = getSelectedEntity();
        var title = edit ? LocalizationProvider.getString("mainframe.button.edit") : LocalizationProvider.getString("mainframe.button.delete");
        if (entity != null && entity.getId() > 0) {
            if (edit) {
                openDialog(entity);
            } else {
                var message = String.format(LocalizationProvider.getString("mainframe.message.confirmtripdelete"), 
                        DateTimeHelper.toDisplayDateFormat(entity.getLocalDate()));
                if (this.userNotificationHandler.askForConfirmation(message, LocalizationProvider.getString("mainframe.button.delete"))) {
                    entity.setDeleted();
                    DatabaseProvider.updateEntity(entity);
                    reloadTable();
                }
            }
        } else {            
            this.userNotificationHandler.showErrorMessage(LocalizationProvider.getString("mainframe.message.notripselected"), title);
        }
    }

    @Override
    protected void performTableSelectionChanged(ListSelectionEvent event) {
        var entity = getSelectedEntity();
        this.tripDataPanel.fillFromEnity((entity == null ? new Trip() : entity));
        
    }
    
    @Override
    protected void performReloadTableViewModel() {
        if (!DatabaseProvider.getIsOpen()) {
            return;
        }
        
        List<Trip> trips;
        
        LocalDate filterStartDate = null;
        LocalDate filterEndDate = null;
        if (this.toggleButtonTripFilterEnabled.isSelected()) {
            var filterType = getCurrentFilterType();
            if (filterType == FilterType.RANGE_FILTER) {
                if (this.datePickerTripFilterStart.isTextFieldValid() && this.datePickerTripFilterEnd.isTextFieldValid()) {
                    filterStartDate = this.datePickerTripFilterStart.getDate();
                    filterEndDate = this.datePickerTripFilterEnd.getDate();
                }
            } else if (filterType == FilterType.MONTH_FILTER || filterType == FilterType.YEAR_FILER) {
                var filterYearString = this.textFieldFilterYear.getText();
                if (!Strings.isNullOrEmpty(filterYearString) && isValidYear(filterYearString)) {
                    int year = Integer.parseInt(filterYearString);
                    if (filterType == FilterType.MONTH_FILTER) {
                        var filterMonth = getCurrentFilterMonth();
                        if (filterMonth != null) {
                            var month = filterMonth.getMonth().getValue();
                            filterStartDate = LocalDate.of(year, month, 1);
                            var calendar = Calendar.getInstance();
                            calendar.set(year, month - 1, 1);
                            var lastDayOfMonth = calendar.getActualMaximum(Calendar.DATE);
                            filterEndDate = LocalDate.of(year, month, lastDayOfMonth);
                        }
                    } else {
                        filterStartDate = LocalDate.of(year, 1, 1);
                        filterEndDate = LocalDate.of(year, 12, 31);
                    }
                }
            }
        }
        
        if (filterStartDate != null & filterEndDate != null) {
            trips = TripManager.getTrips(filterStartDate, filterEndDate);            
        } else {
            trips = TripManager.getTrips();
        }

        setCachedEntities(trips);
        var table = getTable();
        if (table != null) {
            table.setModel(new EntityTableModel<Trip>(trips, this.columnMap));
            var columnModel = table.getColumnModel();
            if (columnModel != null && columnModel.getColumnCount() > 0) {
                var columnDate = columnModel.getColumn(0);
                if (columnDate != null) {
                    columnDate.setMaxWidth(80);
                    columnDate.setMinWidth(80);
                }
            }
        }
    }
    
    private void openDialog(Trip entity) {
        var cachedCustomers = this.customerPanel.getCachedEntities();
        var cachedTripTypes = this.tripTypePanel.getCachedEntities();
        if (cachedCustomers != null && cachedTripTypes != null) {
            this.dialog.showDialog(entity, cachedCustomers.values(), cachedTripTypes.values(), this.mainWindow);
            reloadTable();
        }
    }

    private HashMap<Integer, EntityDataModelHelper<Trip>> createColumnMap() {
        HashMap<Integer, EntityDataModelHelper<Trip>> columnMap = new HashMap<Integer, EntityDataModelHelper<Trip>>();
        columnMap.put(0, new EntityDataModelHelper<Trip>(
                LocalizationProvider.getString("tripbasicinfo.label.date"), (entity) -> DateTimeHelper.toDisplayDateFormat(entity.getLocalDate())));
        columnMap.put(1, new EntityDataModelHelper<Trip>(
                LocalizationProvider.getString("tripbasicinfo.label.customer"), (entity) -> lookupCustomerName(entity.getCustomerId())));
        return columnMap;
    }
    
    private String lookupCustomerName(int customerId) {
        var customers = this.customerPanel.getCachedEntities();       
        if (customers != null && customers.containsKey(customerId)) {
            var customer = customers.get(customerId);
            if (customer != null) {
                return customer.getName();
            }            
        }
        
        return "";
    }
    
    private void processFilterInputChanged(boolean toggleButtonChanged) {
        boolean reload = toggleButtonChanged || this.toggleButtonTripFilterEnabled.isSelected();
        if (reload) {
            reloadTable();
        }
    }
    
    private void processYearFilterInputChanged() {
        var yearString = this.textFieldFilterYear.getText();
        if (!Strings.isNullOrEmpty(yearString) && isValidYear(yearString)) {
            processFilterInputChanged(false);
        }
    }
        
    private static JPanel createFilterPanel(JComponent component, String caption, int width) {
        final var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        final var label = new JLabel(caption + ":");
        final var border = new EmptyBorder(0,0,0,5);
        label.setBorder(border);
        panel.add(label);
        panel.add(component);
        panel.setMaximumSize(new Dimension(width, 25));
        component.setBorder(border);
        return panel;
    }
    
    private FilterType getCurrentFilterType() {
        var result = FilterType.RANGE_FILTER;
        try {
            var selectedItem = this.comboBoxFilterType.getSelectedItem();
            var type = FilterType.class;
            if (selectedItem != null && type.isInstance(selectedItem)) {
                var filterType = type.cast(selectedItem);
                if (filterType != null) {
                    result = filterType;
                }
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }
        
        return result;
    }
    
    private FilterMonth getCurrentFilterMonth() {
        FilterMonth result = null;
        try {
            var selectedItem = this.comboBoxFilterMonth.getSelectedItem();
            var type = FilterMonth.class;
            if (selectedItem != null && type.isInstance(selectedItem)) {
                var filterMonth = type.cast(selectedItem);
                if (filterMonth != null) {
                    result = filterMonth;
                }
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }
        
        return result;
    }
    
    private void performFilterTypeChanged() {
        try {
            var filterType = getCurrentFilterType();
            if (filterType != null) {
                var rangeFilterEnabled =filterType == FilterType.RANGE_FILTER;
                this.panelDatePickerFilterStart.setVisible(rangeFilterEnabled);
                this.panelDatePickerFilterEnd.setVisible(rangeFilterEnabled);
                var monthFilterEnabled = filterType == FilterType.MONTH_FILTER;
                this.panelFilterMonth.setVisible(monthFilterEnabled);
                this.buttonFilterGoToPreviousMonth.setVisible(monthFilterEnabled);
                this.buttonFilterGoToNextMonth.setVisible(monthFilterEnabled);
                this.panelFilterMonthYear.setVisible(monthFilterEnabled || filterType == FilterType.YEAR_FILER);
                processFilterInputChanged(false);
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }
    }
    
    private void performMoveMonth(boolean forward) {
        var selectedIndex = this.comboBoxFilterMonth.getSelectedIndex();
        if (forward) {
            if (selectedIndex != 11) {
                selectedIndex++;
            }
        } else if (selectedIndex != 0) {            
            selectedIndex--;
        }
        
        this.comboBoxFilterMonth.setSelectedIndex(selectedIndex);
    }
    
    private static boolean isValidYear(String value) {
        return Pattern.matches("^[0-9]{4}$", value);
    }
    
    private static class FilterType
    {
        public final static FilterType RANGE_FILTER = new FilterType(LocalizationProvider.getString("trippanel.label.filter.daterange"));
        public final static FilterType MONTH_FILTER = new FilterType(LocalizationProvider.getString("trippanel.label.filter.month"));
        public final static FilterType YEAR_FILER = new FilterType(LocalizationProvider.getString("trippanel.label.filter.year"));
        
        private String name;
        
        public String toString() {
            return this.name;
        }
        
        private FilterType(String name) {
            this.name = name;
        }        
    }
    
    private static class FilterMonth {
        
        public static final List<FilterMonth> MONTHS = creatMonths();
        private final Month month;
        
        public FilterMonth(Month month) {
            this.month = month;
        }
        
        public Month getMonth() {
            return this.month;
        }
        
        public String toString() {
            var locale = LocalizationProvider.getLocale();
            return this.month.getDisplayName(TextStyle.FULL, locale != null ? locale : getDefaultLocale());
        }
        
        private static List<FilterMonth> creatMonths() {
            var months = Month.values();
            if (months != null) {
                var filterMonths = new ArrayList<FilterMonth>(months.length);
                for (var month : months) {
                    filterMonths.add(new FilterMonth(month));
                }
                
                return filterMonths;
            }
            
            return new ArrayList<FilterMonth>(0);
        }
    }
}
