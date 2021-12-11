package de.lordz.java.tools.tdm;

import de.lordz.java.tools.tdm.common.DateTimeHelper;
import de.lordz.java.tools.tdm.common.IUserNotificationHandler;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.common.Logger;
import de.lordz.java.tools.tdm.entities.TravelAllowance;
import jiconfont.icons.font_awesome.FontAwesome;

/**
 * Dialog class for Travel allowance add/delete operation.
 * 
 * @author lordzomat
 *
 */
public class TravelAllowanceDialog extends EntityModelAddOrEditDialogBase<TravelAllowance> {

    private static final long serialVersionUID = 2995570003557084308L;
    final private TravelAllowanceDataPanel dataPanel = new TravelAllowanceDataPanel();
    
    /**
     * Initializes a new instance of the <CODE>TravelAllowanceDialog</CODE> class.
     * 
     * @param userNotificationHandler the user notification handler.
     */
    public TravelAllowanceDialog(IUserNotificationHandler userNotificationHandler) throws IllegalArgumentException {
        super(FontAwesome.MONEY, userNotificationHandler);
        super.setDataComponent(this.dataPanel);
        setBounds(100, 100, 400, 200);
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
        if (entity != null) {
            var validFromDate = entity.getValidFromDate();
            if (entity.getRate() <= 0.0) {
                showErrorMessage(LocalizationProvider.getString("travelallowance.dialog.ratemissing"));
                valid = false;
            } else if (validFromDate == null) {
                showErrorMessage(LocalizationProvider.getString("travelallowance.dialog.validfrommissing"));
                valid = false;
            } else if (entity.getInvalidFromDate() == null) {
                showErrorMessage(LocalizationProvider.getString("travelallowance.dialog.invalidfrommissing"));
                valid = false;
            }
            
            if (valid) {
                var allowance = TripManager.getTravelAllowance(validFromDate);
                if (allowance != null) {
                    if (allowance.getId() > 0) {
                        var message = LocalizationProvider.getString("travelallowance.dialog.validityconflictwithexistingentry");
                        message = String.format(message, DateTimeHelper.toDisplayDateFormat(allowance.getValidFromDate()),
                                DateTimeHelper.toDisplayDateFormat(allowance.getInvalidFromDate()));
                        showErrorMessage(message);
                        valid = false;
                    }
                } else {
                    valid = false;
                }
            }
        } else {
            showErrorMessage(LocalizationProvider.getString("dialog.generalvalidationerror"));
            valid = false;
        }
        
        return valid;
    }
}
