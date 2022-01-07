package de.lordz.java.tools.tdm;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.event.ListSelectionEvent;

import de.lordz.java.tools.tdm.common.IUserNotificationHandler;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.entities.TripType;

/**
 * Panel to manage trip types.
 * 
 * @author lordzomat
 *
 */
public class TripTypePanel extends EntityModelPanelBase<TripType> {

    private static final long serialVersionUID = 4657965064587804086L;
    private static final HashMap<Integer, EntityDataModelHelper<TripType>> columnMap = createColumnMap();
    private final TripTypeDataPanel tripTypeDataPanel;
    private final Component mainWindow;  
    private final IUserNotificationHandler userNotificationHandler;
    private final TripTypeDialog dialog;
    
    /**
     * Initializes a new instance of the <CODE>TripTypePanel</CODE> class.
     * 
     * @param mainWindow The main window component.
     */
    public TripTypePanel(Component mainWindow) {
        super(TripType.class);
        this.userNotificationHandler = (IUserNotificationHandler)mainWindow;
        if (this.userNotificationHandler == null) {
            throw new IllegalArgumentException("Main window does not implement user notification handler!");
        }
        
        this.mainWindow = mainWindow;
        this.dialog = new TripTypeDialog(this.userNotificationHandler);
        this.dialog.setDataSavedActionListener(e -> performReloadTableViewModel());
        this.tripTypeDataPanel = new TripTypeDataPanel();
        this.tripTypeDataPanel.setEditable(false);
        setContentComponent(this.tripTypeDataPanel);
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
                var message = String.format(LocalizationProvider.getString("mainframe.message.confirmtriptypedelete"), entity.getName());
                if (this.userNotificationHandler.askForConfirmation(message, LocalizationProvider.getString("mainframe.button.delete"))) {
                    if (!TripManager.checkIsTripTypeAssigned(entity.getId())) {
                        entity.setDeleted();
                        DatabaseProvider.updateEntity(entity);
                        reloadTable();
                    } else {
                        this.userNotificationHandler.showErrorMessage(
                                LocalizationProvider.getString("triptypedialog.message.deletenotpossiblestillinuse"), title);
                    }
                }
            }
        } else {            
            this.userNotificationHandler.showErrorMessage(LocalizationProvider.getString("mainframe.message.notriptypeselected"), title);
        }
    }

    @Override
    protected void performTableSelectionChanged(ListSelectionEvent event) {
        var entity = getSelectedEntity();
        this.tripTypeDataPanel.fillFromEnity((entity == null ? new TripType() : entity));
        
    }
    
    @Override
    protected void performReloadTableViewModel() {
        if (!DatabaseProvider.getIsOpen()) {
            return;
        }
        
        var tripTypes = TripManager.getTripTypes();
        setCachedEntities(tripTypes);
        var table = getTable();
        if (table != null) {
            table.setModel(new EntityTableModel<TripType>(tripTypes, columnMap));
        }
    }
    
    private void openDialog(TripType entity) {
//        var dialog = new TripTypeDialog2(this.userNotificationHandler);
        this.dialog.showDialog(entity, this.mainWindow);
        reloadTable();
    }

    private static HashMap<Integer, EntityDataModelHelper<TripType>> createColumnMap() {
        HashMap<Integer, EntityDataModelHelper<TripType>> columnMap = new HashMap<Integer, EntityDataModelHelper<TripType>>();
        columnMap.put(0, new EntityDataModelHelper<TripType>(
                LocalizationProvider.getString("triptypebasicinfo.label.name"), (entity) -> entity.getName()));
        return columnMap;
    }
}
