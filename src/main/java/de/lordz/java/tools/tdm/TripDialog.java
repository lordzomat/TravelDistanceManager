package de.lordz.java.tools.tdm;

import java.awt.Component;
import java.util.Collection;

import com.google.common.base.Strings;

import de.lordz.java.tools.tdm.common.IUserNotificationHandler;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.common.Logger;
import de.lordz.java.tools.tdm.entities.Customer;
import de.lordz.java.tools.tdm.entities.Trip;
import de.lordz.java.tools.tdm.entities.TripType;
import jiconfont.icons.font_awesome.FontAwesome;

/**
 * Dialog class for Trip add/delete operation.
 * 
 * @author lordzomat
 *
 */
public class TripDialog extends EntityModelAddOrEditDialogBase<Trip> {

    private static final long serialVersionUID = 2995570003557084308L;
    final private TripDataPanel dataPanel = new TripDataPanel();
    
    /**
     * Initializes a new instance of the <CODE>TripDialog</CODE> class.
     * 
     * @param userNotificationHandler the user notification handler.
     */
    public TripDialog(IUserNotificationHandler userNotificationHandler) throws IllegalArgumentException {
        super(FontAwesome.CAR, userNotificationHandler);
        super.setDataComponent(this.dataPanel);
        setBounds(100, 100, 460, 350);
        this.dataPanel.setEditable(true);
    }
    
    public void showDialog(Trip entity, Collection<Customer> customers, Collection<TripType> tripTypes, Component window) {
        this.dataPanel.reloadReferenceData(customers, tripTypes);
        super.showDialog(entity, window);
    }
    
    @Override
    protected void initializeDialog(Trip entity) {
        try {
            final var titleKey = entity == null ? "tripbasicinfo.title.new" : "tripbasicinfo.title.edit";
            setTitle(LocalizationProvider.getString(titleKey));
            if (entity == null) {
                entity = new Trip();
                setEntity(entity);
            }

            this.dataPanel.fillFromEnity(entity);
        } catch (Exception ex) {
            Logger.Log(ex);
        }   
    }

    @Override
    protected boolean isValid(Trip entity) {
        boolean valid = true;
        if (entity != null) {
            if (entity.getCustomerId() == 0) {
                showErrorMessage(LocalizationProvider.getString("tripdialog.message.nocustomerselected"));
                valid = false;
            } else if (entity.getTripTypeId() == 0) {
                showErrorMessage(LocalizationProvider.getString("tripdialog.message.notriptypeselected"));
                valid = false;
            } else if (Strings.isNullOrEmpty(entity.getTimeOfTrip())) {
                showErrorMessage(LocalizationProvider.getString("tripdialog.message.nodatetimmeselected"));
                valid = false;
            }
        } else {
            showErrorMessage(LocalizationProvider.getString("dialog.generalvalidationerror"));
            valid = false;
        }
        
        return valid;
    }
}
