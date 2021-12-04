package de.lordz.java.tools.tdm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import com.google.common.base.Strings;

import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.common.Logger;
import de.lordz.java.tools.tdm.entities.TripEntity;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

public class TripDialog extends JDialog {

    private static final long serialVersionUID = -3335942655314531982L;
    private final JPanel contentPanel = new JPanel();
    private JButton buttonOk;
    private TripEntity currentTrip;
    private TripBasicInfo tripBasicInfo;
    private boolean dataSaved;

    /**
     * Create the dialog.
     */
    public TripDialog(Collection<CustomerEntity> customers) {
        setBounds(100, 100, 450, 412);
        setResizable(false);
        setIconImage(IconFontSwing.buildImage(FontAwesome.USERS, 15, Color.lightGray));
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        var springLayout = new SpringLayout();
        contentPanel.setLayout(springLayout);
        this.tripBasicInfo= new TripBasicInfo();
        springLayout.putConstraint(SpringLayout.SOUTH, tripBasicInfo, 315, SpringLayout.NORTH, contentPanel);
        this.tripBasicInfo.setCustomers(customers);
        this.tripBasicInfo.setEditable(true);
        springLayout.putConstraint(SpringLayout.NORTH, this.tripBasicInfo, 5, SpringLayout.NORTH, contentPanel);
        springLayout.putConstraint(SpringLayout.WEST, this.tripBasicInfo, 5, SpringLayout.WEST, contentPanel);
        springLayout.putConstraint(SpringLayout.EAST, this.tripBasicInfo, 419, SpringLayout.WEST, contentPanel);
        contentPanel.add(this.tripBasicInfo);

        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                this.buttonOk = new JButton(LocalizationProvider.getString("mainframe.button.accept"));
                this.buttonOk.setActionCommand("OK");
                this.buttonOk.addActionListener(e -> performButtonClick(e));
                buttonPane.add(this.buttonOk);
                getRootPane().setDefaultButton(this.buttonOk);
            }
            {
                JButton cancelButton = new JButton(LocalizationProvider.getString("mainframe.button.cancel"));
                cancelButton.setActionCommand("Cancel");
                cancelButton.addActionListener(e -> performButtonClick(e));
                buttonPane.add(cancelButton);
            }
        }
    }

    public boolean showDialog(TripEntity trip, java.awt.Window window) {
        String titleKey = trip == null ? "tripbasicinfo.title.new" : "tripbasicinfo.title.edit";
        try {
            this.currentTrip = trip != null ? trip : new TripEntity();
            this.tripBasicInfo.fillFromEnity(this.currentTrip);
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        this.setTitle(LocalizationProvider.getString(titleKey));
        this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.setLocationRelativeTo(window);
        this.setVisible(true);

        return this.dataSaved;
    }

    private void performButtonClick(ActionEvent event) {
        boolean closeDialog = true;
        var source = event.getSource();
        if (source != null && source == this.buttonOk) {
            if (this.currentTrip != null) {
                if (this.currentTrip.getCustomerId() == 0) {
                    showErrorMessage(LocalizationProvider.getString("tripdialog.message.nocustomerselected"));
                    closeDialog = false;
                } else if (Strings.isNullOrEmpty(this.currentTrip.getTimeOfTrip())) {
                    showErrorMessage(LocalizationProvider.getString("tripdialog.message.nodatetimmeselected"));
                    closeDialog = false;
                }                
                
                if (closeDialog) {
                    if (this.currentTrip.getId() > 0) {
                        this.dataSaved = DatabaseProvider.updateEntity(this.currentTrip);
                    } else {
                        this.dataSaved = DatabaseProvider.saveEntity(this.currentTrip);
                    }
                }
            }
        }

        if (closeDialog) {
            this.setVisible(false);
        }
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, LocalizationProvider.getString("mainframe.menuitem.opendatabase"),
                JOptionPane.ERROR_MESSAGE);
    }
}
