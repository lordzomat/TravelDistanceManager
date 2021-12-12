package de.lordz.java.tools.tdm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import de.lordz.java.tools.tdm.common.IUserNotificationHandler;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.common.Logger;
import de.lordz.java.tools.tdm.entities.IEntityId;
import jiconfont.IconCode;
import jiconfont.swing.IconFontSwing;

/**
 * Base class for entity add/delete dialogs.
 * 
 * @author lordzomat
 *
 * @param <T> The entity which must implement <CODE>IEntityId</CODE>
 */
public abstract class EntityModelAddOrEditDialogBase<T extends IEntityId> extends JDialog {

    private static final long serialVersionUID = -6946143668242451743L;
    private final JButton buttonOk;
    private final IUserNotificationHandler userNotificationHandler;
    private T currentEntity;
    private boolean dataSaved;

    /**
     * Initializes a new instance of the <CODE>EntityModelAddOrEditDialogBase</CODE> class.
     * 
     * @param iconCode The <CODE>IconCode</CODE> of the dialog.
     * @param userNotificationHandler the user notification handler.
     */
    public EntityModelAddOrEditDialogBase(IconCode iconCode, IUserNotificationHandler userNotificationHandler) throws IllegalArgumentException {
        if (userNotificationHandler == null) {
            throw new IllegalArgumentException("User notification handler is required!");
        }
        
        this.userNotificationHandler = userNotificationHandler;
        setBounds(100, 100, 400, 200);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(IconFontSwing.buildImage(iconCode, 15, Color.lightGray));
        getContentPane().setLayout(new BorderLayout());
        var buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        this.buttonOk = new JButton(LocalizationProvider.getString("mainframe.button.accept"));
        this.buttonOk.setActionCommand("OK");
        this.buttonOk.addActionListener(e -> performButtonClick(e));
        buttonPane.add(this.buttonOk);
        getRootPane().setDefaultButton(this.buttonOk);
        var cancelButton = new JButton(LocalizationProvider.getString("mainframe.button.cancel"));
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(e -> performButtonClick(e));
        buttonPane.add(cancelButton);
    }

    /**
     * Shows the dialog for the given entity.
     * 
     * @param entity The entity.
     * @param window The main window component.
     * @return Returns <CODE>true</CODE> if dialog was closed with accept button.
     */
    public boolean showDialog(T entity, Component window) {
        setEntity(entity);
        initializeDialog(entity);

        this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.setLocationRelativeTo(window);
        this.setVisible(true);

        return this.dataSaved;
    }
        
    /**
     * Sets the data component of the dialog.
     * 
     * @param component The component to set.
     */
    protected void setDataComponent(Component component) {
        var panel = new JPanel(new BorderLayout(0,0));
        panel.add(component, BorderLayout.WEST);
        panel.setBorder(new EmptyBorder(5, 0, 0, 0));
        getContentPane().add(panel, BorderLayout.NORTH);
    }
    
    /**
     * Sets the entity.
     * 
     * @param entity
     */
    protected void setEntity(T entity) {
        this.currentEntity = entity;
    }
    
    /**
     * Retrieves the current entity.
     * 
     * @return The entity.
     */
    protected T getEntity() {
        return this.currentEntity;
    }
    
    /**
     * Shows a error message to the user.
     * 
     * @param message
     */
    protected void showErrorMessage(String message) {
        this.userNotificationHandler.showErrorMessage(this, message, getTitle());
    }
    
    /**
     * Initializes the dialog.
     * 
     * @param entity The entity for which the dialog has to be initialized.
     */
    protected abstract void initializeDialog(T entity);
    
    
    /**
     * Checks whether the entity is valid and can be accepted.
     * 
     * @param entity The entity to check.
     * @return Returns <CODE>true</CODE> if the entity is valid, otherwise <CODE>false</CODE>
     */
    protected abstract boolean isValid(T entity);

    private void performButtonClick(ActionEvent event) {
        try {
            boolean closeDialog = true;
            var source = event.getSource();
            if (source != null && source == this.buttonOk) {
                if (this.currentEntity != null) {
                    closeDialog = isValid(this.currentEntity);
                    if (closeDialog) {
                        if (this.currentEntity.getId() > 0) {
                            this.dataSaved = DatabaseProvider.updateEntity(this.currentEntity);
                        } else {
                            this.dataSaved = DatabaseProvider.saveEntity(this.currentEntity);
                        }
                    }
                }
            }
    
            if (closeDialog) {
                this.setVisible(false);
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }
    }
}
