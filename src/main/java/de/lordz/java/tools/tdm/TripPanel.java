package de.lordz.java.tools.tdm;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;

import com.github.lgooddatepicker.components.DatePicker;

import de.lordz.java.tools.tdm.common.DateTimeHelper;
import de.lordz.java.tools.tdm.common.IUserNotificationHandler;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
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
    private JToggleButton toggleButtonTripFilterEnabled;
    private DatePicker datePickerTripFilterStart;
    private DatePicker datePickerTripFilterEnd;
    
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
        this.toggleButtonTripFilterEnabled.addItemListener(e -> processTripFilterInputChanged(true));
        addToolbarComponent(this.toggleButtonTripFilterEnabled);
        
        this.datePickerTripFilterStart = DateTimeHelper.createDatePicker();
        this.datePickerTripFilterStart.setDate(LocalDate.of(LocalDate.now().getYear(), 1, 1));
        this.datePickerTripFilterStart.addDateChangeListener(e -> processTripFilterInputChanged(false));
        var panelDatePickerFilterStart = createDatePickerPanel(this.datePickerTripFilterStart,
                LocalizationProvider.getString("trippanel.datepicker.tooltip.from"));
        addToolbarComponent(panelDatePickerFilterStart);
        
        this.datePickerTripFilterEnd = DateTimeHelper.createDatePicker();
        this.datePickerTripFilterEnd.setDate(LocalDate.of(LocalDate.now().getYear(), 12, 31));
        this.datePickerTripFilterEnd.addDateChangeListener(e -> processTripFilterInputChanged(false));
        var panelDatePickerFilterEnd = createDatePickerPanel(this.datePickerTripFilterEnd,
                LocalizationProvider.getString("trippanel.datepicker.tooltip.until"));
        addToolbarComponent(panelDatePickerFilterEnd);
        setContentComponent(this.tripDataPanel);
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
        if (this.toggleButtonTripFilterEnabled.isSelected() && 
                this.datePickerTripFilterStart.isTextFieldValid() &&
                this.datePickerTripFilterEnd.isTextFieldValid()) {            
            trips = TripManager.getTrips(this.datePickerTripFilterStart.getDate(), this.datePickerTripFilterEnd.getDate());            
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
    
    private void processTripFilterInputChanged(boolean toggleButtonChanged) {
        boolean reload = toggleButtonChanged || this.toggleButtonTripFilterEnabled.isSelected();
        if (reload) {
            reloadTable();;
        }
    }
    
    private static JPanel createDatePickerPanel(DatePicker datePicker, String caption) {
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
}
