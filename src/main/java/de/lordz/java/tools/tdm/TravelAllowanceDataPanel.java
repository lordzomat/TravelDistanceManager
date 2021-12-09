package de.lordz.java.tools.tdm;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.lordz.java.tools.tdm.common.DateTimeHelper;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.entities.TravelAllowance;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
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
public class TravelAllowanceDataPanel extends JPanel {

    private static final long serialVersionUID = -8141107873337917764L;
    private TravelAllowance currentEntity;
    private boolean editMode;
    private JTextField textFieldValue;
    private DatePicker datePickerValidFrom;
    private DatePicker datePickerInvalidFrom;
    private JLabel labelValidFrom;
    private JLabel labelInvalidFrom;

    /**
     * Initializes a new instance of the <CODE>TravelAllowanceDataPanel</CODE> class.
     */
    public TravelAllowanceDataPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {100, 150};
        gridBagLayout.rowHeights = new int[] {20, 20, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 1.0};
        gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0};
        setLayout(gridBagLayout);
        
        JLabel labelCustomer = new JLabel(Beans.isDesignTime() ? "value" : LocalizationProvider.getString("travelallowancebasicinfo.label.value"));
        GridBagConstraints constraintLabelCustomer = new GridBagConstraints();
        constraintLabelCustomer.anchor = GridBagConstraints.WEST;
        constraintLabelCustomer.fill = GridBagConstraints.VERTICAL;
        constraintLabelCustomer.insets = new Insets(0, 0, 5, 5);
        constraintLabelCustomer.gridx = 0;
        constraintLabelCustomer.gridy = 0;
        add(labelCustomer, constraintLabelCustomer);
        
        this.textFieldValue = new JTextField();
        GridBagConstraints constraintName = new GridBagConstraints();
        constraintName.anchor = GridBagConstraints.WEST;
        constraintName.insets = new Insets(0, 0, 5, 0);
        constraintName.gridx = 1;
        constraintName.gridy = 0;
        add(this.textFieldValue, constraintName);
        this.textFieldValue.setColumns(10);
        
        labelValidFrom = new JLabel(Beans.isDesignTime() ? "validfrom" : LocalizationProvider.getString("travelallowancebasicinfo.label.validfrom"));
        GridBagConstraints constraintLabelValidFrom = new GridBagConstraints();
        constraintLabelValidFrom.anchor = GridBagConstraints.WEST;
        constraintLabelValidFrom.insets = new Insets(0, 0, 5, 5);
        constraintLabelValidFrom.gridx = 0;
        constraintLabelValidFrom.gridy = 1;
        add(labelValidFrom, constraintLabelValidFrom);
        
        this.datePickerValidFrom = Beans.isDesignTime() ? new DatePicker() : DateTimeHelper.createDatePicker();
        GridBagConstraints constraintDatePickerValidFrom = new GridBagConstraints();
        constraintDatePickerValidFrom.anchor = GridBagConstraints.NORTHWEST;
        constraintDatePickerValidFrom.insets = new Insets(0, 0, 5, 0);
        constraintDatePickerValidFrom.gridx = 1;
        constraintDatePickerValidFrom.gridy = 1;
        add(this.datePickerValidFrom, constraintDatePickerValidFrom);
        
        labelInvalidFrom = new JLabel(Beans.isDesignTime() ? "invalidFrom" : LocalizationProvider.getString("travelallowancebasicinfo.label.invalidfrom"));
        var constraintLabelInvalidFrom = new GridBagConstraints();
        constraintLabelInvalidFrom.anchor = GridBagConstraints.WEST;
        constraintLabelInvalidFrom.insets = new Insets(0, 0, 0, 5);
        constraintLabelInvalidFrom.gridx = 0;
        constraintLabelInvalidFrom.gridy = 2;
        add(labelInvalidFrom, constraintLabelInvalidFrom);
        
        this.datePickerInvalidFrom = Beans.isDesignTime() ? new DatePicker() : DateTimeHelper.createDatePicker();
        var constraintDatePickerInvalidFrom = new GridBagConstraints();
        constraintDatePickerInvalidFrom.anchor = GridBagConstraints.NORTHWEST;
        constraintDatePickerInvalidFrom.gridx = 1;
        constraintDatePickerInvalidFrom.gridy = 2;
        add(this.datePickerInvalidFrom, constraintDatePickerInvalidFrom);
    }

    /**
     * Fills in the trip entity data.
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
