package de.lordz.java.tools.tdm;

import java.awt.BorderLayout;
import java.awt.Dialog;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import de.lordz.java.tools.tdm.common.LocalizationProvider;

/**
 * Dialog to show please wait and block (Modal).
 * 
 * @author lordz
 *
 */
class WaitDialog {

    private JDialog dialog;

    public void showDialog(String title, java.awt.Window window) {
        this.dialog = new JDialog(window, title, Dialog.ModalityType.APPLICATION_MODAL);

        var progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        var panel = new JPanel(new BorderLayout());
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(new JLabel(LocalizationProvider.getString("waitdialog.pleasewait")), BorderLayout.PAGE_START);
        this.dialog.add(panel);
        this.dialog.setUndecorated(true);
        this.dialog.pack();
        this.dialog.setSize(100, 50);
        this.dialog.setLocationRelativeTo(window);
        this.dialog.setVisible(true);
    }

    public void close() {
        this.dialog.dispose();
    }
}