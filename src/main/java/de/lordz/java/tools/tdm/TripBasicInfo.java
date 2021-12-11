package de.lordz.java.tools.tdm;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.lordz.java.tools.tdm.common.DateTimeHelper;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.entities.*;
import com.github.lgooddatepicker.components.DatePicker;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;

/**
 * Panel to handle basic trip info.
 * 
 * @author lordzomat
 *
 */
public class TripBasicInfo extends JPanel {

    private static final long serialVersionUID = 6012453072233623623L;
    private JComboBox<Customer> comboBoxCustomer;
    private JComboBox<TripType> comboBoxTripType;
    private ArrayList<Customer> currentCustomerEntities;
    private ArrayList<TripType> currentTripTypeEntities;
    private Trip currentTrip;
    private boolean editMode;
    private DatePicker datePicker;
    private JTextArea textAreaDescription;

    public TripBasicInfo() {
        var gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {100, 250, 0};
        gridBagLayout.rowHeights = new int[] {20, 20, 20, 200, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);
        
        var labelCustomer = new JLabel(LocalizationProvider.getString("tripbasicinfo.label.customer"));
        var constraintLabelCustomer = new GridBagConstraints();
        constraintLabelCustomer.fill = GridBagConstraints.BOTH;
        constraintLabelCustomer.insets = new Insets(0, 0, 5, 5);
        constraintLabelCustomer.gridx = 0;
        constraintLabelCustomer.gridy = 0;
        add(labelCustomer, constraintLabelCustomer);
        
        this.comboBoxCustomer = new JComboBox<Customer>();
        var constraintCustomer = new GridBagConstraints();
        constraintCustomer.fill = GridBagConstraints.BOTH;
        constraintCustomer.insets = new Insets(0, 0, 5, 0);
        constraintCustomer.gridx = 1;
        constraintCustomer.gridy = 0;
        add(this.comboBoxCustomer, constraintCustomer);
        
        var labelTripType = new JLabel("Trip Type");
        var constraintLabelTripType = new GridBagConstraints();
        constraintLabelTripType.fill = GridBagConstraints.BOTH;
        constraintLabelTripType.insets = new Insets(0, 0, 5, 5);
        constraintLabelTripType.gridx = 0;
        constraintLabelTripType.gridy = 1;
        add(labelTripType, constraintLabelTripType);
        
        this.comboBoxTripType = new JComboBox<TripType>();
        var constraintTripType = new GridBagConstraints();
        constraintTripType.fill = GridBagConstraints.BOTH;
        constraintTripType.insets = new Insets(0, 0, 5, 0);
        constraintTripType.gridx = 1;
        constraintTripType.gridy = 1;
        add(this.comboBoxTripType, constraintTripType);
        
        var labelDate = new JLabel(LocalizationProvider.getString("tripbasicinfo.label.date"));
        var constraintLabelDate = new GridBagConstraints();
        constraintLabelDate.fill = GridBagConstraints.BOTH;
        constraintLabelDate.insets = new Insets(0, 0, 5, 5);
        constraintLabelDate.gridx = 0;
        constraintLabelDate.gridy = 2;
        add(labelDate, constraintLabelDate);
        
        var panelDatePicker = new JPanel();
        this.datePicker = DateTimeHelper.createDatePicker();
        var constraintDatePicker = new GridBagConstraints();
        constraintDatePicker.fill = GridBagConstraints.BOTH;
        constraintDatePicker.insets = new Insets(0, 0, 5, 0);
        constraintDatePicker.gridx = 1;
        constraintDatePicker.gridy = 2;
        add(panelDatePicker, constraintDatePicker);
        panelDatePicker.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
        panelDatePicker.add(this.datePicker);
                
        var panelDescription = new JPanel();
        var labelDescription = new JLabel(LocalizationProvider.getString("tripbasicinfo.label.description"));
        var constrainctLabelDescription = new GridBagConstraints();
        constrainctLabelDescription.fill = GridBagConstraints.BOTH;
        constrainctLabelDescription.insets = new Insets(0, 0, 0, 5);
        constrainctLabelDescription.gridx = 0;
        constrainctLabelDescription.gridy = 3;
        add(panelDescription, constrainctLabelDescription);
        panelDescription.add(labelDescription);
        panelDescription.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        
        var scrollPane = new JScrollPane();
        this.textAreaDescription = new JTextArea();
        var constraintDescription = new GridBagConstraints();
        constraintDescription.fill = GridBagConstraints.BOTH;
        constraintDescription.gridx = 1;
        constraintDescription.gridy = 3;
        add(scrollPane, constraintDescription);
        this.textAreaDescription.setColumns(10);
        scrollPane.setViewportView(this.textAreaDescription);
    }

    /**
     * Reloads referenced data e.g. customers and trip type.s.
     * 
     * @param customerEntities The list containing all customers.
     * @param tripTypes The list containing all trip types.
     */
    public void reloadReferenceData(Collection<Customer> customerEntities, Collection<TripType> tripTypes) {
        this.comboBoxCustomer.removeAllItems();
        this.currentCustomerEntities = new ArrayList<Customer>(customerEntities);
        if (customerEntities != null) {
            for (Customer customerEntity : customerEntities) {
                this.comboBoxCustomer.addItem(customerEntity);
            }
        }
        
        this.comboBoxTripType.removeAllItems();
        this.currentTripTypeEntities = new ArrayList<TripType>(tripTypes);
        if (tripTypes != null) {
            for (TripType tripType : tripTypes) {
                this.comboBoxTripType.addItem(tripType);
            }
        }
    }

    /**
     * Fills in the trip entity data.
     * 
     * @param customer The entity from which the data is taken.
     */
    public void fillFromEnity(Trip entity) {
        if (entity != null) {
            int customerId = entity.getCustomerId();
            setSelectedComboBoxItem(this.comboBoxCustomer, this.currentCustomerEntities, customerId, e -> e.getId());
            int tripTypeId = entity.getTripTypeId();
            setSelectedComboBoxItem(this.comboBoxTripType, this.currentTripTypeEntities, tripTypeId, e -> e.getId());

            this.textAreaDescription.setText(entity.getDescription());
            var localDate = entity.getLocalDate();
            if (localDate != null) {
                this.datePicker.setDate(localDate);
            } else {
                this.datePicker.clear();
            }
        } else {
            entity = new Trip();
        }

        if (this.editMode) {
            this.currentTrip = entity;
            setSelectedCustomer();
            setSelectedTripType();
            this.textAreaDescription.getDocument()
                    .addDocumentListener(new EntityTextChangeDocumentListener((value) -> this.currentTrip.setDescription(value)));
            this.comboBoxCustomer.addActionListener(e -> setSelectedCustomer());
            this.comboBoxTripType.addActionListener(e -> setSelectedTripType());
            this.datePicker.addDateChangeListener(e -> setTimeOfTrip());
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
        this.comboBoxTripType.setEnabled(editable);
        this.datePicker.setEnabled(editable);
        this.textAreaDescription.setEditable(editable);
        var border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        this.textAreaDescription.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(2, 2, 2, 2)));
    }
    
    private void setSelectedCustomer() {
        setSelectedComboBoxItem(this.comboBoxCustomer, Customer.class, id -> this.currentTrip.setCustomerId(id));
    }
    
    private void setSelectedTripType() {
        setSelectedComboBoxItem(this.comboBoxTripType, TripType.class, id -> this.currentTrip.setTripTypeId(id));
    }

    private <T extends IEntityId> void setSelectedComboBoxItem(JComboBox<T> comboBox, Class<? extends T> type, Consumer<Integer> setId) {
        if (comboBox != null && setId != null) {
            var selectedItem = comboBox.getSelectedItem();
            if (selectedItem != null && type.isInstance(selectedItem)) {
                var entity = type.cast(selectedItem);
                if (entity != null) {
                    setId.accept(entity.getId());
                }
            }
        }
    }
    
    private void setTimeOfTrip() {
        if (this.datePicker.isTextFieldValid()) {
            this.currentTrip.setTimeOfTrip(DateTimeHelper.toSortableDateTime(this.datePicker.getDate()));
        }
    }
    
    private <T> void setSelectedComboBoxItem(JComboBox<T> comboBox, List<T> list, int selectedId, Function<T, Integer> getEntityId) {
        if (list != null && selectedId > 0) {
            boolean customerFound = false;
            for (int i = 0; i < list.size() && !customerFound; i++) {
                var item = list.get(i);
                if (item != null && getEntityId.apply(item) == selectedId) {
                    comboBox.setSelectedIndex(i);
                    customerFound = true;
                }
            }
        } else if (selectedId == 0) {
            comboBox.setSelectedIndex(-1);
        }
    }
}
