package de.lordz.java.tools.tdm;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.entities.CustomerEntity;
import de.lordz.java.tools.tdm.entities.TripEntity;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateHighlightPolicy;
import com.github.lgooddatepicker.zinternaltools.HighlightInformation;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.awt.FlowLayout;

/**
 * Panel to handle basic customer info.
 * 
 * @author lordzomat
 *
 */
public class TripBasicInfo extends JPanel {

    private static final long serialVersionUID = 6012453072233623623L;
    private JComboBox<CustomerEntity> comboBoxCustomer;
    private ArrayList<CustomerEntity> currentCustomerEntities;
    private TripEntity currentTrip;
    private boolean editMode;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private JTextArea textAreaDescription;

    public TripBasicInfo() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {100, 250, 0};
        gridBagLayout.rowHeights = new int[] {20, 20, 20, 200, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);
        
        JLabel labelCustomer = new JLabel(LocalizationProvider.getString("tripbasicinfo.label.customer"));
        GridBagConstraints constraintLabelCustomer = new GridBagConstraints();
        constraintLabelCustomer.fill = GridBagConstraints.BOTH;
        constraintLabelCustomer.insets = new Insets(0, 0, 5, 5);
        constraintLabelCustomer.gridx = 0;
        constraintLabelCustomer.gridy = 0;
        add(labelCustomer, constraintLabelCustomer);
        
        this.comboBoxCustomer = new JComboBox<CustomerEntity>();
        GridBagConstraints constraintCustomer = new GridBagConstraints();
        constraintCustomer.fill = GridBagConstraints.BOTH;
        constraintCustomer.insets = new Insets(0, 0, 5, 0);
        constraintCustomer.gridx = 1;
        constraintCustomer.gridy = 0;
        add(this.comboBoxCustomer, constraintCustomer);
        
        JLabel labelDate = new JLabel(LocalizationProvider.getString("tripbasicinfo.label.date"));
        GridBagConstraints constraintLabelDate = new GridBagConstraints();
        constraintLabelDate.fill = GridBagConstraints.BOTH;
        constraintLabelDate.insets = new Insets(0, 0, 5, 5);
        constraintLabelDate.gridx = 0;
        constraintLabelDate.gridy = 1;
        add(labelDate, constraintLabelDate);
        
        var dateSettings = new DatePickerSettings();
        dateSettings.setHighlightPolicy(new WeekendHighlightPolicy());
        dateSettings.setFormatForDatesCommonEra("dd.MM.yyyy");
        var panelDatePicker = new JPanel();
        this.datePicker = new DatePicker(dateSettings);
        GridBagConstraints constraintDatePicker = new GridBagConstraints();
        constraintDatePicker.fill = GridBagConstraints.BOTH;
        constraintDatePicker.insets = new Insets(0, 0, 5, 0);
        constraintDatePicker.gridx = 1;
        constraintDatePicker.gridy = 1;
        add(panelDatePicker, constraintDatePicker);
        panelDatePicker.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
        panelDatePicker.add(this.datePicker);
        
        JLabel labelTime = new JLabel(LocalizationProvider.getString("tripbasicinfo.label.time"));
        GridBagConstraints constraintLabelTime = new GridBagConstraints();
        constraintLabelTime.fill = GridBagConstraints.BOTH;
        constraintLabelTime.insets = new Insets(0, 0, 5, 5);
        constraintLabelTime.gridx = 0;
        constraintLabelTime.gridy = 2;
        add(labelTime, constraintLabelTime);
        
        var timeSettings = new TimePickerSettings();
        timeSettings.setDisplayToggleTimeMenuButton(false);
        timeSettings.setDisplaySpinnerButtons(true);
        timeSettings.setInitialTimeToNow();
        var panelTimePicker = new JPanel();
        this.timePicker = new TimePicker(timeSettings);
        GridBagConstraints constraintTimePicker = new GridBagConstraints();
        constraintTimePicker.fill = GridBagConstraints.BOTH;
        constraintTimePicker.insets = new Insets(0, 0, 5, 0);
        constraintTimePicker.gridx = 1;
        constraintTimePicker.gridy = 2;
        add(panelTimePicker, constraintTimePicker);
        panelTimePicker.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
        panelTimePicker.add(this.timePicker);
        
        var panelDescription = new JPanel();
        var labelDescription = new JLabel(LocalizationProvider.getString("tripbasicinfo.label.description"));
        GridBagConstraints constrainctLabelDescription = new GridBagConstraints();
        constrainctLabelDescription.fill = GridBagConstraints.BOTH;
        constrainctLabelDescription.insets = new Insets(0, 0, 0, 5);
        constrainctLabelDescription.gridx = 0;
        constrainctLabelDescription.gridy = 3;
        add(panelDescription, constrainctLabelDescription);
        panelDescription.add(labelDescription);
        panelDescription.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        
        var scrollPane = new JScrollPane();
        this.textAreaDescription = new JTextArea();
        GridBagConstraints constraintDescription = new GridBagConstraints();
        constraintDescription.fill = GridBagConstraints.BOTH;
        constraintDescription.gridx = 1;
        constraintDescription.gridy = 3;
        add(scrollPane, constraintDescription);
        this.textAreaDescription.setColumns(10);
        scrollPane.setViewportView(this.textAreaDescription);
        this.timePicker.clear();
    }

    /**
     * Sets the customers for selection.
     * 
     * @param customerEntities The list containing all customers.
     */
    public void setCustomers(Collection<CustomerEntity> customerEntities) {
        this.comboBoxCustomer.removeAllItems();
        this.currentCustomerEntities = new ArrayList<CustomerEntity>(customerEntities);
        if (customerEntities != null) {
            for (CustomerEntity customerEntity : customerEntities) {
                this.comboBoxCustomer.addItem(customerEntity);
            }
        }
    }

    /**
     * Fills in the trip entity data.
     * 
     * @param customer The entity from which the data is taken.
     */
    public void fillFromEnity(TripEntity entity) {
        if (entity != null) {
            int customerId = entity.getCustomerId();
            if (this.currentCustomerEntities != null && customerId > 0) {
                boolean customerFound = false;
                for (int i = 0; i < this.currentCustomerEntities.size() && !customerFound; i++) {
                    var customerEntity = this.currentCustomerEntities.get(i);
                    if (customerEntity != null && customerEntity.getId() == customerId) {
                        this.comboBoxCustomer.setSelectedIndex(i);
                        customerFound = true;
                    }
                }
            } else if (customerId == 0) {
                this.comboBoxCustomer.setSelectedIndex(-1);
            }

            this.textAreaDescription.setText(entity.getDescription());
            var localDate = entity.getLocalDate();
            if (localDate != null) {
                this.datePicker.setDate(localDate);
            } else {
                this.datePicker.clear();
            }
            
            var localTime = entity.getLocalTime();
            if (localTime != null) {
                this.timePicker.setTime(localTime);
            } else {
                this.timePicker.clear();
            }
        } else {
            entity = new TripEntity();
        }

        if (this.editMode) {
            this.currentTrip = entity;
            setSelectedCustomer();
            this.textAreaDescription.getDocument()
                    .addDocumentListener(new EntityTextChangeDocumentListener((value) -> this.currentTrip.setDescription(value)));
            this.comboBoxCustomer.addActionListener(e -> setSelectedCustomer());            
            this.datePicker.addDateChangeListener(e -> setTimeOfTrip());
            this.timePicker.addTimeChangeListener(e -> setTimeOfTrip());
        }
    }

    /**
     * Specifies if the text of text components is editable or not.
     * 
     * @param editable If true the text fields can be edited.
     */
    public void setEditable(boolean editable) {
        this.editMode = editable;
        this.comboBoxCustomer.setEnabled(editable);
        this.datePicker.setEnabled(editable);
        this.timePicker.setEnabled(editable);
        this.textAreaDescription.setEditable(editable);
        var border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        this.textAreaDescription.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(2, 2, 2, 2)));
    }

    private void setSelectedCustomer() {
        var selectedItem = this.comboBoxCustomer.getSelectedItem();
        if (selectedItem != null && selectedItem instanceof CustomerEntity) {
            var customerEntity = (CustomerEntity)selectedItem;
            this.currentTrip.setCustomerId(customerEntity.getId());
        }
    }
    
    private void setTimeOfTrip() {
        if (this.datePicker.isTextFieldValid() && this.timePicker.isTextFieldValid()) {
            this.currentTrip.setTimeOfTrip(this.datePicker.getDateStringOrEmptyString() + "T" + this.timePicker.getTimeStringOrEmptyString());
        }
    }
   
    private static class WeekendHighlightPolicy implements DateHighlightPolicy {

        @Override
        public HighlightInformation getHighlightInformationOrNull(LocalDate date) {
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                return new HighlightInformation(Color.LIGHT_GRAY, Color.BLACK, null);
            }
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                return new HighlightInformation(Color.GRAY, Color.BLACK, null);
            }
            
            return null;
        }
    }
}
