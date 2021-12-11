package de.lordz.java.tools.tdm;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.entities.Customer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import com.google.common.base.Strings;

/**
 * Panel to hold and handle customer data.
 * 
 * @author lordzomat
 *
 */
public class CustomerDataPanel extends GridBagDataPanelBase {

    private static final long serialVersionUID = 5203491756280623451L;
    private JTextField textFieldName;
    private JTextField textFieldStreet;
    private JTextField textFieldPostcode;
    private JTextField textFieldCity;
    private JTextField textFieldDistance;
    private JTextArea textAreaDescription;
    private Customer currentCustomer;
    private boolean editMode;

    /**
     * Initializes a new instance of the <CODE>CustomerDataPanel</CODE> class.
     */
    public CustomerDataPanel() {
        super(new int[] {100, 250}, new int[] {20, 20, 20, 20, 20, 160}, new double[]{0.0, 1.0}, new double[]{0.0, 1.0, 1.0, 1.0, 1.0, 1.0});
        
        this.textFieldName = new JTextField();
        this.textFieldName.setColumns(10);
        AddLabel(0, 0, LocalizationProvider.getString("customerdialog.label.name"));
        AddInput(1, 0, this.textFieldName, GridBagConstraints.HORIZONTAL);
        this.textFieldStreet = new JTextField();
        this.textFieldStreet.setColumns(10);
        AddLabel(0, 1, LocalizationProvider.getString("customerdialog.label.street"));
        AddInput(1, 1, this.textFieldStreet, GridBagConstraints.HORIZONTAL);
        this.textFieldPostcode = new JTextField();
        this.textFieldPostcode.setColumns(10);
        AddLabel(0, 2, LocalizationProvider.getString("customerdialog.label.postcode"));
        AddInput(1, 2, this.textFieldPostcode, GridBagConstraints.HORIZONTAL);
        this.textFieldCity = new JTextField();
        this.textFieldCity.setColumns(10);
        AddLabel(0, 3, LocalizationProvider.getString("customerdialog.label.city"));
        AddInput(1, 3, this.textFieldCity, GridBagConstraints.HORIZONTAL);
        this.textFieldDistance = new JTextField();
        this.textFieldDistance.setColumns(10);
        AddLabel(0, 4, LocalizationProvider.getString("customerdialog.label.distance"));
        AddInput(1, 4, this.textFieldDistance);
        
        var scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(250, 160));
        this.textAreaDescription = new JTextArea();
        this.textAreaDescription.setColumns(10);
        scrollPane.setViewportView(this.textAreaDescription);
        AddLabel(0, 5, LocalizationProvider.getString("customerdialog.label.description"));
        AddInput(1, 5, scrollPane, GridBagConstraints.BOTH);
    }

    /**
     * Fills in the customer entity data.
     * 
     * @param customer The entity from which the data is taken.
     */
    public void fillFromEnity(Customer customer) {
        if (customer != null) {        
            setText(this.textFieldName, customer.getName());
            setText(this.textFieldStreet, customer.getStreet());
            setText(this.textFieldPostcode, customer.getPostcode());
            setText(this.textFieldCity, customer.getCity());
            setText(this.textFieldDistance, Double.toString(customer.getDistance()));
            setText(this.textAreaDescription, customer.getDescription());
        } else {
            customer = new Customer();
        }

        if (this.editMode) {
            this.currentCustomer = customer;
            this.textFieldName.getDocument().addDocumentListener(
                    new EntityTextChangeDocumentListener((value) -> this.currentCustomer.setName(value)));
            this.textFieldStreet.getDocument().addDocumentListener(
                    new EntityTextChangeDocumentListener((value) -> this.currentCustomer.setStreet(value)));
            this.textFieldPostcode.getDocument().addDocumentListener(
                    new EntityTextChangeDocumentListener((value) -> this.currentCustomer.setPostcode(value)));
            this.textFieldCity.getDocument().addDocumentListener(
                    new EntityTextChangeDocumentListener((value) -> this.currentCustomer.setCity(value)));
            this.textFieldDistance.getDocument()
                    .addDocumentListener(new EntityTextChangeDocumentListener((value) -> updateDistance(value)));
            this.textAreaDescription.getDocument().addDocumentListener(
                    new EntityTextChangeDocumentListener((value) -> this.currentCustomer.setDescription(value)));
        }
    }
    
    
    /**
     * Specifies if the text of text components is editable or not.
     * 
     * @param editable If true the text fields can be edited.
     */
    public void setEditable(boolean editable) {
        this.editMode = editable;
        var border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        for (Component item : getComponents()) {
            if (item != null && item instanceof JTextComponent) {
                var textComponent = (JTextComponent) item;
                textComponent.setEditable(editable);
            }
        }

        this.textAreaDescription.setEditable(editable);
        this.textAreaDescription.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(2, 2, 2, 2)));
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

    }
    
    private void updateDistance(String value) {
        if (!Strings.isNullOrEmpty(value)) {
            value = value.replace(',', '.');
            this.currentCustomer.setDistance(Double.parseDouble(value));
        } else {
            this.currentCustomer.setDistance(0.0);
        }
    }
}
