package de.lordz.java.tools.tdm;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.lordz.java.tools.tdm.common.DateTimeHelper;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.entities.Customer;
import de.lordz.java.tools.tdm.entities.IEntityId;
import de.lordz.java.tools.tdm.entities.Trip;
import de.lordz.java.tools.tdm.entities.TripType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.github.lgooddatepicker.components.DatePicker;

/**
 * Panel to hold and handle trip data.
 * 
 * @author lordzomat
 *
 */
public class TripDataPanel extends GridBagDataPanelBase {

    private static final long serialVersionUID = -6656227064606325111L;
    private JComboBox<Customer> comboBoxCustomer;
    private JComboBox<TripType> comboBoxTripType;
    private DatePicker datePicker;
    private JTextArea textAreaDescription;
    private ArrayList<Customer> currentCustomerEntities;
    private ArrayList<TripType> currentTripTypeEntities;
    private Trip currentTrip;
    private boolean editMode;

    /**
     * Initializes a new instance of the <CODE>TripDataPanel</CODE> class.
     */
    public TripDataPanel() {
        super(new int[] {100, 250}, new int[] {20, 20, 20, 160}, new double[]{0.0, 1.0}, new double[]{0.0, 1.0, 1.0, 1.0});
        
        this.comboBoxCustomer = new JComboBox<Customer>();
        var prototypeCustomer = new Customer();
        prototypeCustomer.setName("##########");
        // used to get a fixed size of the combo box so it does not expand with it's content.
        this.comboBoxCustomer.setPrototypeDisplayValue(prototypeCustomer);
        AddLabel(0, 0, LocalizationProvider.getString("tripbasicinfo.label.customer"));
        AddInput(1, 0, this.comboBoxCustomer, GridBagConstraints.HORIZONTAL);
        this.comboBoxTripType = new JComboBox<TripType>();
        var prototypeTripType = new TripType();
        prototypeTripType.setName("##########");
        this.comboBoxTripType.setPrototypeDisplayValue(prototypeTripType);
        AddLabel(0, 1, LocalizationProvider.getString("tripbasicinfo.label.triptype"));
        AddInput(1, 1, this.comboBoxTripType, GridBagConstraints.HORIZONTAL);
        this.datePicker = DateTimeHelper.createDatePicker();
        AddLabel(0, 2, LocalizationProvider.getString("tripbasicinfo.label.date"));
        AddInput(1, 2, this.datePicker, GridBagConstraints.NONE);
        var scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(250, 160));
        this.textAreaDescription = new JTextArea();
        this.textAreaDescription.setColumns(10);
        scrollPane.setViewportView(this.textAreaDescription);
        AddLabel(0, 3, LocalizationProvider.getString("tripbasicinfo.label.description"));
        AddInput(1, 3, scrollPane, GridBagConstraints.BOTH);
    }
    
    /**
     * Reloads referenced data e.g. customers and trip types.
     * 
     * @param customerEntities The list containing all customers.
     * @param tripTypes The list containing all trip types.
     */
    public void reloadReferenceData(Collection<Customer> customerEntities, Collection<TripType> tripTypes) {
        if (customerEntities != null) {
            this.comboBoxCustomer.removeAllItems();
            this.currentCustomerEntities = new ArrayList<Customer>(customerEntities);
            if (customerEntities != null) {
                for (Customer customerEntity : customerEntities) {
                    this.comboBoxCustomer.addItem(customerEntity);
                }
            }
        }
        
        if (tripTypes != null) {
            this.comboBoxTripType.removeAllItems();
            this.currentTripTypeEntities = new ArrayList<TripType>(tripTypes);
            if (tripTypes != null) {
                for (TripType tripType : tripTypes) {
                    this.comboBoxTripType.addItem(tripType);
                }
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
