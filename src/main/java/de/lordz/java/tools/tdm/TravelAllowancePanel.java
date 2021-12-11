package de.lordz.java.tools.tdm;

import java.awt.Component;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.swing.event.ListSelectionEvent;

import de.lordz.java.tools.tdm.common.DateTimeHelper;
import de.lordz.java.tools.tdm.common.IUserNotificationHandler;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.entities.TravelAllowance;

/**
 * Panel to manage travel allowances.
 * 
 * @author lordzomat
 *
 */
public class TravelAllowancePanel extends EntityModelPanelBase<TravelAllowance> {

    private static final long serialVersionUID = -4717340337935228403L;
    private static final HashMap<Integer, EntityDataModelHelper<TravelAllowance>> columnMap = createColumnMap();
    private final TravelAllowanceDataPanel travelAllowanceDataPanel;
    private final Component mainWindow;  
    private final IUserNotificationHandler userNotificationHandler;
    private final TravelAllowanceDialog dialog;
    
    /**
     * Initializes a new instance of the <CODE>TravelAllowancePanel</CODE> class.
     * 
     * @param mainWindow The main window component.
     */
    public TravelAllowancePanel(Component mainWindow) {
        super(TravelAllowance.class);
        this.userNotificationHandler = (IUserNotificationHandler)mainWindow;
        if (this.userNotificationHandler == null) {
            throw new IllegalArgumentException("Main window does not implement user notification handler!");
        }
        
        this.mainWindow = mainWindow;
        this.dialog = new TravelAllowanceDialog(this.userNotificationHandler);
        this.travelAllowanceDataPanel = new TravelAllowanceDataPanel();
        this.travelAllowanceDataPanel.setEditable(false);
        setContentComponent(this.travelAllowanceDataPanel);
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
                var message = String.format(LocalizationProvider.getString("travelallowance.message.confirmdelete"),
                        new DecimalFormat("0.00").format(entity.getRate()));
                if (this.userNotificationHandler.askForConfirmation(message, LocalizationProvider.getString("mainframe.button.delete"))) {
                    entity.setDeleted();
                    DatabaseProvider.updateEntity(entity);
                    reloadTable();
                }
            }
        } else {            
            this.userNotificationHandler.showErrorMessage(LocalizationProvider.getString("mainframe.message.notravelallowaneselected"), title);
        }
    }

    @Override
    protected void performTableSelectionChanged(ListSelectionEvent event) {
        var entity = getSelectedEntity();
        this.travelAllowanceDataPanel.fillFromEnity((entity == null ? new TravelAllowance() : entity));
        
    }
    
    @Override
    protected void performReloadTableViewModel() {
        if (!DatabaseProvider.getIsOpen()) {
            return;
        }
        
        var travelAllowances = TripManager.getTravelAllowances();
        setCachedEntities(travelAllowances);
        var table = getTable();
        if (table != null) {
            table.setModel(new EntityTableModel<TravelAllowance>(travelAllowances, columnMap));
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
    
    private void openDialog(TravelAllowance entity) {
        this.dialog.showDialog(entity, this.mainWindow);
        reloadTable();
    }

    private static HashMap<Integer, EntityDataModelHelper<TravelAllowance>> createColumnMap() {
        HashMap<Integer, EntityDataModelHelper<TravelAllowance>> columnMap = new HashMap<Integer, EntityDataModelHelper<TravelAllowance>>();
        columnMap.put(0, new EntityDataModelHelper<TravelAllowance>(LocalizationProvider.getString("travelallowancebasicinfo.label.value"),
                (entity) -> entity.getRate()));
        columnMap.put(1, new EntityDataModelHelper<TravelAllowance>(LocalizationProvider.getString("travelallowancebasicinfo.label.validfrom"),
                (entity) -> DateTimeHelper.toDisplayDateFormat(entity.getValidFromDate())));
        columnMap.put(2, new EntityDataModelHelper<TravelAllowance>(LocalizationProvider.getString("travelallowancebasicinfo.label.invalidfrom"),
                (entity) -> DateTimeHelper.toDisplayDateFormat(entity.getInvalidFromDate())));
        return columnMap;
    }
}
