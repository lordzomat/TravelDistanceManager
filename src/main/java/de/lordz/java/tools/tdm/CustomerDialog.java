package de.lordz.java.tools.tdm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import com.google.common.base.Strings;

import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.common.Logger;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

public class CustomerDialog extends JDialog {

    private static final long serialVersionUID = -3335942655314531982L;
    private final JPanel contentPanel = new JPanel();
    private JButton buttonOk;
    private CustomerEntity currentCustomer;
    private CustomerBasicInfo customerBasicInfo;
    private boolean dataSaved;

    /**
     * Create the dialog.
     */
    public CustomerDialog() {
        setBounds(100, 100, 450, 350);
        setResizable(false);
        setIconImage(IconFontSwing.buildImage(FontAwesome.USERS, 15, Color.lightGray));
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        var springLayout = new SpringLayout();
        contentPanel.setLayout(springLayout);
        this.customerBasicInfo = new CustomerBasicInfo();
        this.customerBasicInfo.setEditable(true);
        springLayout.putConstraint(SpringLayout.SOUTH, customerBasicInfo, 263, SpringLayout.NORTH, contentPanel);
        springLayout.putConstraint(SpringLayout.NORTH, this.customerBasicInfo, 5, SpringLayout.NORTH, contentPanel);
        springLayout.putConstraint(SpringLayout.WEST, this.customerBasicInfo, 5, SpringLayout.WEST, contentPanel);
        springLayout.putConstraint(SpringLayout.EAST, customerBasicInfo, 419, SpringLayout.WEST, contentPanel);
        contentPanel.add(this.customerBasicInfo);

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

    public boolean showDialog(CustomerEntity customer, java.awt.Window window) {
        String titleKey = customer == null ? "customerdialog.title.new" : "customerdialog.title.edit";
        try {
            this.currentCustomer = customer != null ? customer : new CustomerEntity();
            this.customerBasicInfo.fillFromEnity(this.currentCustomer);
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
            if (this.currentCustomer != null) {
                if (Strings.isNullOrEmpty(this.currentCustomer.getName())) {
                    showErrorMessage(LocalizationProvider.getString("customerdialog.message.namemissing"));
                    closeDialog = false;
                } else if (this.currentCustomer.getDistance() <= 0.0) {
                    showErrorMessage(LocalizationProvider.getString("customerdialog.message.distancemissing"));
                    closeDialog = false;
                }

                if (closeDialog) {
                    if (this.currentCustomer.getId() > 0) {
                        this.dataSaved = DatabaseProvider.updateEntity(this.currentCustomer);
                    } else {
                        this.dataSaved = DatabaseProvider.saveEntity(this.currentCustomer);
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
