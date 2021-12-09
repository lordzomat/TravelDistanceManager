package de.lordz.java.tools.tdm;

import de.lordz.java.tools.tdm.common.IUserNotificationHandler;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.common.Logger;
import de.lordz.java.tools.tdm.entities.TravelAllowance;

/**
 * Dialog class for Travel allowance add/delete operation.
 * 
 * @author lordzomat
 *
 */
public class TravelAllowanceDialog extends EntityModelAddOrEditDialogBase<TravelAllowance> {

    private static final long serialVersionUID = 2995570003557084308L;
    final private TravelAllowanceDataPanel dataPanel = new TravelAllowanceDataPanel();
    final private IUserNotificationHandler userNotificationHandler;
    
    /**
     * Initializes a new instance of the <CODE>TravelAllowanceDialog</CODE> class.
     * 
     * @param userNotificationHandler the user notification handler.
     */
    public TravelAllowanceDialog(IUserNotificationHandler userNotificationHandler) throws IllegalArgumentException {
        super.setDataComponent(this.dataPanel);
        setBounds(100, 100, 400, 200);
        if (userNotificationHandler == null) {
            throw new IllegalArgumentException("User notification handler is required!");
        }
        
        this.userNotificationHandler = userNotificationHandler;
        this.dataPanel.setEditable(true);
    }
    
    @Override
    protected void initializeDialog(TravelAllowance entity) {
        try {
            final var titleKey = entity == null ? "travelallowancebasicinfo.title.new" : "travelallowancebasicinfo.title.edit";
            setTitle(LocalizationProvider.getString(titleKey));
            if (entity == null) {
                entity = new TravelAllowance();
                setEntity(entity);
            }

            this.dataPanel.fillFromEnity(entity);
        } catch (Exception ex) {
            Logger.Log(ex);
        }   
    }

    @Override
    protected boolean isValid(TravelAllowance entity) {
        boolean valid = true;
        if (entity.getRate() <= 0.0) {
            showErrorMessage(LocalizationProvider.getString("travelallowance.dialog.ratemissing"));
            valid = false;
        } else if (entity.getValidFromDate() == null) {
            showErrorMessage(LocalizationProvider.getString("travelallowance.dialog.validfrommissing"));
            valid = false;
        } else if (entity.getInvalidFromDate() == null) {
            showErrorMessage(LocalizationProvider.getString("travelallowance.dialog.invalidfrommissing"));
            valid = false;
        }
        
        return valid;
    }
    
    private void showErrorMessage(String message) {
        this.userNotificationHandler.showErrorMessage(this, message, getTitle());
    }
}
