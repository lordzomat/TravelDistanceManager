package de.lordz.java.tools.tdm;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.event.ListSelectionEvent;
import de.lordz.java.tools.tdm.common.IUserNotificationHandler;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.entities.Customer;

/**
 * Panel to manage customers.
 * 
 * @author lordzomat
 *
 */
public class CustomerPanel extends EntityModelPanelBase<Customer> {

    private static final long serialVersionUID = 7246998456049917201L;
    private static final HashMap<Integer, EntityDataModelHelper<Customer>> columnMap = createColumnMap();
    private final CustomerDataPanel customerDataPanel;
    private final Component mainWindow;  
    private final IUserNotificationHandler userNotificationHandler;
    private final CustomerDialog dialog;
    
    /**
     * Initializes a new instance of the <CODE>CustomerPanel</CODE> class.
     * 
     * @param mainWindow The main window component.
     */
    public CustomerPanel(Component mainWindow) {
        super(Customer.class);
        this.userNotificationHandler = (IUserNotificationHandler)mainWindow;
        if (this.userNotificationHandler == null) {
            throw new IllegalArgumentException("Main window does not implement user notification handler!");
        }
        
        this.mainWindow = mainWindow;
        this.dialog = new CustomerDialog(this.userNotificationHandler);
        this.customerDataPanel = new CustomerDataPanel();
        this.customerDataPanel.setEditable(false);
        setContentComponent(this.customerDataPanel);
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
                var message = String.format(LocalizationProvider.getString("mainframe.message.confirmcustomerdelete"), entity.getName());
                if (this.userNotificationHandler.askForConfirmation(message, title)) {
                    if (!TripManager.checkIsCustomerAssignd(entity.getId())) {
                        entity.setDeleted();
                        DatabaseProvider.updateEntity(entity);
                        reloadTable();
                    } else {
                        this.userNotificationHandler.showErrorMessage(
                                LocalizationProvider.getString("customerdialog.message.deletenotpossiblestillinuse"), title);
                    }
                }
            }
        } else {            
            this.userNotificationHandler.showErrorMessage(LocalizationProvider.getString("mainframe.message.nocustomerselected"), title);
        }
    }

    @Override
    protected void performTableSelectionChanged(ListSelectionEvent event) {
        var entity = getSelectedEntity();
        this.customerDataPanel.fillFromEnity((entity == null ? new Customer() : entity));
        
    }
    
    @Override
    protected void performReloadTableViewModel() {
        if (!DatabaseProvider.getIsOpen()) {
            return;
        }
        
        var customers = CustomerManager.getCustomers();
        setCachedEntities(customers);
        var table = getTable();
        if (table != null) {
            table.setModel(new EntityTableModel<Customer>(customers, columnMap));
            var columnModel = table.getColumnModel();
            if (columnModel != null && columnModel.getColumnCount() > 0) {
                var columnDistance = columnModel.getColumn(2);
                if (columnDistance != null) {
                    columnDistance.setMaxWidth(100);
                    columnDistance.setMinWidth(100);
                    columnDistance.setCellRenderer(RightTableCellRenderer);
                }
            }
        }
    }
    
    private void openDialog(Customer entity) {
        this.dialog.showDialog(entity, this.mainWindow);
        reloadTable();
    }
    
    private static HashMap<Integer, EntityDataModelHelper<Customer>> createColumnMap() {
        HashMap<Integer, EntityDataModelHelper<Customer>> columnMap = new HashMap<Integer, EntityDataModelHelper<Customer>>();
        columnMap.put(0, new EntityDataModelHelper<Customer>(
                LocalizationProvider.getString("customerdialog.label.name"), (entity) -> entity.getName()));
        columnMap.put(1, new EntityDataModelHelper<Customer>(
                LocalizationProvider.getString("customerdialog.label.city"), (entity) -> entity.getCity()));
        columnMap.put(2, new EntityDataModelHelper<Customer>(
                LocalizationProvider.getString("customerdialog.label.distance"), (entity) -> entity.getDistance()));
        return columnMap;
    }
}
