package de.lordz.java.tools.tdm;

import com.google.common.base.Strings;

import de.lordz.java.tools.tdm.common.IUserNotificationHandler;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.common.Logger;
import de.lordz.java.tools.tdm.entities.TripType;
import jiconfont.icons.font_awesome.FontAwesome;

/**
 * Dialog class for trip type add/delete operation.
 * 
 * @author lordzomat
 *
 */
public class TripTypeDialog extends EntityModelAddOrEditDialogBase<TripType> {

    private static final long serialVersionUID = 531187912095704754L;
    final private TripTypeDataPanel dataPanel = new TripTypeDataPanel();
    
    /**
     * Initializes a new instance of the <CODE>TripTypeDialog</CODE> class.
     * 
     * @param userNotificationHandler the user notification handler.
     */
    public TripTypeDialog(IUserNotificationHandler userNotificationHandler) {
        super(FontAwesome.FOLDER_O, userNotificationHandler);
        super.setDataComponent(this.dataPanel);
        setBounds(100, 100, 400, 200);
        this.dataPanel.setEditable(true);
    }
    
    @Override
    protected void initializeDialog(TripType entity) {
        try {
            final var titleKey = entity == null ? "triptypebasicinfo.title.new" : "triptypebasicinfo.title.edit";
            setTitle(LocalizationProvider.getString(titleKey));
            if (entity == null) {
                entity = new TripType();
                setEntity(entity);
            }

            this.dataPanel.fillFromEnity(entity);
        } catch (Exception ex) {
            Logger.Log(ex);
        }   
    }

    @Override
    protected boolean isValid(TripType entity) {
        boolean valid = true;
        if (entity != null) {
            if (Strings.isNullOrEmpty(entity.getName())) {
                showErrorMessage(LocalizationProvider.getString("triptypedialog.message.namemissing"));
                valid = false;
            }
        } else {
            showErrorMessage(LocalizationProvider.getString("dialog.generalvalidationerror"));
            valid = false;
        }
        
        return valid;
    }
}
