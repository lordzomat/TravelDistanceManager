package de.lordz.java.tools.tdm;

import de.lordz.java.tools.tdm.common.DateTimeHelper;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.entities.TravelAllowance;
import java.beans.Beans;
import javax.swing.JTextField;
import com.github.lgooddatepicker.components.DatePicker;
import com.google.common.base.Strings;

/**
 * Panel to hold and handle travel allowance data.
 * 
 * @author lordzomat
 *
 */
public class TravelAllowanceDataPanel extends GridBagDataPanelBase {

    private static final long serialVersionUID = -8141107873337917764L;
    private TravelAllowance currentEntity;
    private boolean editMode;
    private JTextField textFieldValue;
    private DatePicker datePickerValidFrom;
    private DatePicker datePickerInvalidFrom;

    /**
     * Initializes a new instance of the <CODE>TravelAllowanceDataPanel</CODE> class.
     */
    public TravelAllowanceDataPanel() {
        super(new int[] { 100, 150 }, new int[] { 20, 20, 0 }, new double[] { 0.0, 1.0 }, new double[] { 0.0, 1.0, 1.0 });
        this.textFieldValue = new JTextField();
        this.textFieldValue.setColumns(10);
        AddLabel(0, 0, Beans.isDesignTime() ? "value" : LocalizationProvider.getString("travelallowancebasicinfo.label.value"));
        AddInput(1, 0, this.textFieldValue);
        this.datePickerValidFrom = Beans.isDesignTime() ? new DatePicker() : DateTimeHelper.createDatePicker();
        AddLabel(0, 1, Beans.isDesignTime() ? "value" : LocalizationProvider.getString("travelallowancebasicinfo.label.validfrom"));
        AddInput(1, 1, this.datePickerValidFrom);
        this.datePickerInvalidFrom = Beans.isDesignTime() ? new DatePicker() : DateTimeHelper.createDatePicker();
        AddLabel(0, 2, Beans.isDesignTime() ? "value" : LocalizationProvider.getString("travelallowancebasicinfo.label.invalidfrom"));
        AddInput(1, 2, this.datePickerInvalidFrom);
    }

    /**
     * Fills in the travel allowance entity data.
     * 
     * @param customer The entity from which the data is taken.
     */
    public void fillFromEnity(TravelAllowance entity) {
        if (entity != null) {
            this.textFieldValue.setText(String.valueOf(entity.getRate()));
            this.datePickerValidFrom.setDate(entity.getValidFromDate());
            this.datePickerInvalidFrom.setDate(entity.getInvalidFromDate());
        } else {
            entity = new TravelAllowance();
        }

        if (this.editMode) {
            this.currentEntity = entity;
            this.textFieldValue.getDocument().addDocumentListener(new EntityTextChangeDocumentListener((value) -> setRate(value)));
            this.datePickerValidFrom.addDateChangeListener(e -> setDate(true));
            this.datePickerInvalidFrom.addDateChangeListener(e -> setDate(false));
        }
    }
    
    
    /**
     * Specifies if the text of text components is editable or not.
     * 
     * @param editable If true the text fields can be edited.
     */
    public void setEditable(boolean editable) {
        this.editMode = editable;
        this.textFieldValue.setEditable(editable);
        this.datePickerValidFrom.setEnabled(editable);
        this.datePickerInvalidFrom.setEnabled(editable);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

    }
    
    private void setRate(String value) {
        double rate = 0.0;
        if (!Strings.isNullOrEmpty(value)) {
            value = value.replace(',', '.');
            rate = Double.parseDouble(value);
        }
        
        this.currentEntity.setRate(rate);
    }
    
    private void setDate(boolean validFrom) {
        if (validFrom) {
            if (this.datePickerValidFrom.isTextFieldValid()) {
                this.currentEntity.setValidFromDate(DateTimeHelper.toSortableDateTime(this.datePickerValidFrom.getDate()));;
            }            
        } else {
            if (this.datePickerInvalidFrom.isTextFieldValid()) {
                this.currentEntity.setInvalidFromDate((DateTimeHelper.toSortableDateTime(this.datePickerInvalidFrom.getDate())));
            }       
        }
  }
}
