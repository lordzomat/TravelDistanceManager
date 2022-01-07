package de.lordz.java.tools.tdm;

import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.entities.TripType;

import java.beans.Beans;

import javax.swing.JTextField;

/**
 * Panel to hold and handle trip type data.
 * 
 * @author lordzomat
 *
 */
public class TripTypeDataPanel extends GridBagDataPanelBase {

    private static final long serialVersionUID = 6012453072233623623L;
    private TripType currentTripType;
    private JTextField textFieldName;
    private boolean editMode;
    private boolean listenersInitialized;

    /**
     * Initializes a new instance of the <CODE>TripTypeDataPanel</CODE> class.
     */
    public TripTypeDataPanel() {
        super(new int[] { 100, 150 }, new int[] { 20, }, new double[] { 0.0, 1.0 }, new double[] { 0.0 });
        this.textFieldName = new JTextField();
        this.textFieldName.setColumns(10);
        AddLabel(0, 0, Beans.isDesignTime() ? "value" : LocalizationProvider.getString("triptypebasicinfo.label.name"));
        AddInput(1, 0, this.textFieldName);
    }

    /**
     * Fills in the trip type entity data.
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
            if (!this.listenersInitialized) {
                this.textFieldName.getDocument().addDocumentListener(
                        new EntityTextChangeDocumentListener((value) -> this.currentTripType.setName(value)));
                this.listenersInitialized = true;
            }
            
            this.textFieldName.requestFocusInWindow();
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
