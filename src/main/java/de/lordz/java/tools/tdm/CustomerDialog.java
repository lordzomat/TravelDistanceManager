package de.lordz.java.tools.tdm;

import com.google.common.base.Strings;

import de.lordz.java.tools.tdm.common.IUserNotificationHandler;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.common.Logger;
import de.lordz.java.tools.tdm.entities.Customer;
import jiconfont.icons.font_awesome.FontAwesome;

/**
 * Dialog class for customer add/delete operation.
 * 
 * @author lordzomat
 *
 */
public class CustomerDialog extends EntityModelAddOrEditDialogBase<Customer> {

    private static final long serialVersionUID = -8000651901698752169L;
    final private CustomerDataPanel dataPanel = new CustomerDataPanel();
    
    /**
     * Initializes a new instance of the <CODE>CustomerDialog</CODE> class.
     * 
     * @param userNotificationHandler the user notification handler.
     */
    public CustomerDialog(IUserNotificationHandler userNotificationHandler) throws IllegalArgumentException {
        super(FontAwesome.USERS, userNotificationHandler);
        super.setDataComponent(this.dataPanel);
        setBounds(100, 100, 400, 400);
        this.dataPanel.setEditable(true);
    }
    
    @Override
    protected void initializeDialog(Customer entity) {
        try {
            final var titleKey = entity == null ? "customerdialog.title.new" : "customerdialog.title.edit";
            setTitle(LocalizationProvider.getString(titleKey));
            if (entity == null) {
                entity = new Customer();
                setEntity(entity);
            }

            this.dataPanel.fillFromEnity(entity);
        } catch (Exception ex) {
            Logger.Log(ex);
        }   
    }

    @Override
    protected boolean isValid(Customer entity) {
        boolean valid = true;
        if (entity != null) {
            if (Strings.isNullOrEmpty(entity.getName())) {
                showErrorMessage(LocalizationProvider.getString("customerdialog.message.namemissing"));
                valid = false;
            } else if (entity.getDistance() <= 0.0) {
                showErrorMessage(LocalizationProvider.getString("customerdialog.message.distancemissing"));
                valid = false;
            }
        } else {
            showErrorMessage(LocalizationProvider.getString("dialog.generalvalidationerror"));
            valid = false;
        }
        
        return valid;
    }
}
