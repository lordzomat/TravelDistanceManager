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
import de.lordz.java.tools.tdm.entities.TripType;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

public class TripTypeDialog extends JDialog {

    private static final long serialVersionUID = -7451260822567939012L;
    private JButton buttonOk;
    private TripType currentTripType;
    private TripTypeBasicInfo basicInfo;
    private boolean dataSaved;

    /**
     * Create the dialog.
     */
    public TripTypeDialog() {
        setBounds(100, 100, 450, 149);
        setResizable(false);
        setIconImage(IconFontSwing.buildImage(FontAwesome.FOLDER_O, 15, Color.lightGray));
        getContentPane().setLayout(new BorderLayout());
        var contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        var springLayout = new SpringLayout();
        contentPanel.setLayout(springLayout);
        this.basicInfo= new TripTypeBasicInfo();
        springLayout.putConstraint(SpringLayout.SOUTH, basicInfo, 58, SpringLayout.NORTH, contentPanel);
        this.basicInfo.setEditable(true);
        springLayout.putConstraint(SpringLayout.NORTH, this.basicInfo, 5, SpringLayout.NORTH, contentPanel);
        springLayout.putConstraint(SpringLayout.WEST, this.basicInfo, 5, SpringLayout.WEST, contentPanel);
        springLayout.putConstraint(SpringLayout.EAST, this.basicInfo, 419, SpringLayout.WEST, contentPanel);
        contentPanel.add(this.basicInfo);

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

    public boolean showDialog(TripType tripType, java.awt.Window window) {
        String titleKey = tripType == null ? "triptypebasicinfo.title.new" : "triptypebasicinfo.title.edit";
        try {
            this.currentTripType = tripType != null ? tripType : new TripType();
            this.basicInfo.fillFromEnity(this.currentTripType);
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
            if (this.currentTripType != null) {
                if (Strings.isNullOrEmpty(this.currentTripType.getName())) {
                    showErrorMessage(LocalizationProvider.getString("triptypedialog.message.namemissing"));
                    closeDialog = false;
                }
                
                if (closeDialog) {
                    if (this.currentTripType.getId() > 0) {
                        this.dataSaved = DatabaseProvider.updateEntity(this.currentTripType);
                    } else {
                        this.dataSaved = DatabaseProvider.saveEntity(this.currentTripType);
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
