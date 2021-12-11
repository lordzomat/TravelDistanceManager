package de.lordz.java.tools.tdm;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.entities.TripType;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;

/**
 * Panel to hold and handle trip type data.
 * 
 * @author lordzomat
 *
 */
public class TripTypeDataPanel extends JPanel {

    private static final long serialVersionUID = 6012453072233623623L;
    private TripType currentTripType;
    private boolean editMode;
    private JTextField textFieldName;

    public TripTypeDataPanel() {
        var gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {100, 150};
        gridBagLayout.rowHeights = new int[] {20};
        gridBagLayout.columnWeights = new double[]{0.0, 1.0};
        gridBagLayout.rowWeights = new double[]{0.0};
        setLayout(gridBagLayout);
        
        var labelCustomer = new JLabel(LocalizationProvider.getString("triptypebasicinfo.label.name"));
        var constraintLabelCustomer = new GridBagConstraints();
        constraintLabelCustomer.anchor = GridBagConstraints.WEST;
        constraintLabelCustomer.fill = GridBagConstraints.VERTICAL;
        constraintLabelCustomer.insets = new Insets(0, 5, 5, 5);
        constraintLabelCustomer.gridx = 0;
        constraintLabelCustomer.gridy = 0;
        add(labelCustomer, constraintLabelCustomer);
        
        this.textFieldName = new JTextField();
        var constraintName = new GridBagConstraints();
        constraintName.insets = new Insets(0, 0, 5, 0);
        constraintName.anchor = GridBagConstraints.WEST;
        constraintName.fill = GridBagConstraints.VERTICAL;
        constraintName.gridx = 1;
        constraintName.gridy = 0;
        add(this.textFieldName, constraintName);
        this.textFieldName.setColumns(10);
    }

    /**
     * Fills in the trip entity data.
     * 
     * @param customer The entity from which the data is taken.
     */
    public void fillFromEnity(TripType entity) {
        if (entity != null) {

            this.textFieldName.setText(entity.getName());
        } else {
            entity = new TripType();
        }

        if (this.editMode) {
            this.currentTripType = entity;
            this.textFieldName.getDocument()
                    .addDocumentListener(new EntityTextChangeDocumentListener((value) -> this.currentTripType.setName(value)));
        }
    }

    /**
     * Specifies if the text of text components is editable or not.
     * 
     * @param editable If true the text fields can be edited.
     */
    public void setEditable(boolean editable) {
        this.editMode = editable;
        this.textFieldName.setEditable(editable);
    }
}
