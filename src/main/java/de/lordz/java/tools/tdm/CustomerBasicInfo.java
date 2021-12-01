package de.lordz.java.tools.tdm;

import java.awt.Component;
import java.awt.Color;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import com.google.common.base.Strings;

/**
 * Panel to handel basic customer info.
 * 
 * @author lordz
 *
 */
public class CustomerBasicInfo extends JPanel {

	private static final long serialVersionUID = 5327206289101137863L;
	private JTextField textFieldName;
	private JTextField textFieldStreet;
	private JTextField textFieldPostcode;
	private JTextField textFieldCity;
	private JTextField textFieldDistance;
	private JScrollPane textAreaScrollPane;
	private JTextArea textAreaDescription;
	private CustomerEntity currentCustomer;
	private boolean editMode;
		
	public CustomerBasicInfo() {
		var springLayout = new SpringLayout();
		setLayout(springLayout);		
		
		var labelName = new JLabel(LocalizationProvider.GetString("customerdialog.label.name") + ":");
		springLayout.putConstraint(SpringLayout.NORTH, labelName, 5, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelName, 5, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, labelName, 31, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, labelName, 89, SpringLayout.WEST, this);
		add(labelName);
		
		this.textFieldName = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, this.textFieldName, 2, SpringLayout.NORTH, labelName);
		springLayout.putConstraint(SpringLayout.WEST, this.textFieldName, 6, SpringLayout.EAST, labelName);
		springLayout.putConstraint(SpringLayout.EAST, this.textFieldName, 217, SpringLayout.WEST, this);
		add(this.textFieldName);
		
		var labelStreet = new JLabel(LocalizationProvider.GetString("customerdialog.label.street") + ":");
		springLayout.putConstraint(SpringLayout.WEST, labelStreet, 0, SpringLayout.WEST, labelName);
		add(labelStreet);
		
		this.textFieldStreet = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, labelStreet, 3, SpringLayout.NORTH, this.textFieldStreet);
		springLayout.putConstraint(SpringLayout.NORTH, this.textFieldStreet, 6, SpringLayout.SOUTH, this.textFieldName);
		springLayout.putConstraint(SpringLayout.WEST, this.textFieldStreet, 0, SpringLayout.WEST, this.textFieldName);
		springLayout.putConstraint(SpringLayout.EAST, this.textFieldStreet, 0, SpringLayout.EAST, this.textFieldName);
		add(this.textFieldStreet);
		
		var labelPostcode = new JLabel(LocalizationProvider.GetString("customerdialog.label.postcode") + ":");
		springLayout.putConstraint(SpringLayout.WEST, labelPostcode, 0, SpringLayout.WEST, labelName);
		add(labelPostcode);
		
		this.textFieldPostcode = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, labelPostcode, 3, SpringLayout.NORTH, this.textFieldPostcode);
		springLayout.putConstraint(SpringLayout.NORTH, this.textFieldPostcode, 6, SpringLayout.SOUTH, this.textFieldStreet);
		springLayout.putConstraint(SpringLayout.WEST, this.textFieldPostcode, 0, SpringLayout.WEST, this.textFieldName);
		springLayout.putConstraint(SpringLayout.EAST, this.textFieldPostcode, 0, SpringLayout.EAST, this.textFieldName);
		add(this.textFieldPostcode);
		
		var labelCity = new JLabel(LocalizationProvider.GetString("customerdialog.label.city") + ":");
		springLayout.putConstraint(SpringLayout.WEST, labelCity, 0, SpringLayout.WEST, labelName);
		add(labelCity);
		
		textFieldCity = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, labelCity, 3, SpringLayout.NORTH, this.textFieldCity);
		springLayout.putConstraint(SpringLayout.NORTH, this.textFieldCity, 6, SpringLayout.SOUTH, this.textFieldPostcode);
		springLayout.putConstraint(SpringLayout.WEST, this.textFieldCity, 0, SpringLayout.WEST, this.textFieldName);
		springLayout.putConstraint(SpringLayout.EAST, this.textFieldCity, 0, SpringLayout.EAST, this.textFieldName);
		add(this.textFieldCity);
		this.textFieldCity.setColumns(10);
		
		this.textFieldDistance = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, this.textFieldDistance, 6, SpringLayout.SOUTH, this.textFieldCity);
		springLayout.putConstraint(SpringLayout.WEST, this.textFieldDistance, 0, SpringLayout.WEST, this.textFieldName);
		springLayout.putConstraint(SpringLayout.EAST, this.textFieldDistance, 0, SpringLayout.EAST, this.textFieldName);
		this.textFieldDistance.setColumns(10);
		add(this.textFieldDistance);
		
		var labelDistance = new JLabel(LocalizationProvider.GetString("customerdialog.label.distance") + ":");
		springLayout.putConstraint(SpringLayout.NORTH, labelDistance, 3, SpringLayout.NORTH, this.textFieldDistance);
		springLayout.putConstraint(SpringLayout.WEST, labelDistance, 0, SpringLayout.WEST, labelName);
		add(labelDistance);
		
		JLabel labelDescription = new JLabel(LocalizationProvider.GetString("customerdialog.label.description") + ":");
		springLayout.putConstraint(SpringLayout.NORTH, labelDescription, 14, SpringLayout.SOUTH, labelDistance);
		springLayout.putConstraint(SpringLayout.WEST, labelDescription, 0, SpringLayout.WEST, labelName);
		add(labelDescription);
		
		textAreaScrollPane = new JScrollPane();
		this.textAreaDescription = new JTextArea();
		this.textAreaScrollPane.setViewportView(this.textAreaDescription);
		springLayout.putConstraint(SpringLayout.SOUTH, this.textAreaScrollPane, -87, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, this.textAreaScrollPane, 192, SpringLayout.EAST, this.textFieldDistance);
		springLayout.putConstraint(SpringLayout.NORTH, this.textAreaScrollPane, 6, SpringLayout.SOUTH, this.textFieldDistance);
		springLayout.putConstraint(SpringLayout.WEST, this.textAreaScrollPane, 0, SpringLayout.WEST, this.textFieldDistance);
		this.textAreaDescription.setColumns(10);
		add(this.textAreaScrollPane);
	}
	
	/**
	 * Fills in the customer entity data.
	 * 
	 * @param customer The entity from which the data is taken.
	 */
	public void fillFromEnity(CustomerEntity customer) {
		if (customer != null) {
			this.textFieldName.setText(customer.getName());
			this.textFieldStreet.setText(customer.getStreet());				
			this.textFieldPostcode.setText(customer.getPostcode());
			this.textFieldCity.setText(customer.getCity());
			this.textFieldDistance.setText(Double.toString(customer.getDistance()));
			this.textAreaDescription.setText(customer.getDescription());
		} else {
			customer = new CustomerEntity();
		}
		
		if (this.editMode) { 
			this.currentCustomer = customer;
			this.textFieldName.getDocument().addDocumentListener(new EntityTextChangeDocumentListener((value) -> this.currentCustomer.setName(value)));
			this.textFieldStreet.getDocument().addDocumentListener(new EntityTextChangeDocumentListener((value) -> this.currentCustomer.setStreet(value)));				
			this.textFieldPostcode.getDocument().addDocumentListener(new EntityTextChangeDocumentListener((value) -> this.currentCustomer.setPostcode(value)));
			this.textFieldCity.getDocument().addDocumentListener(new EntityTextChangeDocumentListener((value) -> this.currentCustomer.setCity(value)));				
			this.textFieldDistance.getDocument().addDocumentListener(new EntityTextChangeDocumentListener((value) -> UpdateDistance(value)));
			this.textAreaDescription.getDocument().addDocumentListener(new EntityTextChangeDocumentListener((value) -> this.currentCustomer.setDescription(value)));
		} else {
			
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
				var textComponent = (JTextComponent)item;
				textComponent.setEditable(editable);

			}
		}
		
		this.textAreaDescription.setEditable(editable);
		this.textAreaDescription.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(2, 2, 2, 2)));
	}
	
	private void UpdateDistance(String value)
	{
		if (!Strings.isNullOrEmpty(value)) {
			value = value.replace(',', '.');
			this.currentCustomer.setDistance(Double.parseDouble(value));
		} else {
			this.currentCustomer.setDistance(0.0);	
		}
	}
	
	class EntityTextChangeDocumentListener implements DocumentListener {
	    private Consumer<String> consumer;
	 
	    public EntityTextChangeDocumentListener(Consumer<String> consumer) {
	    	this.consumer = consumer;
	    }
	    
	    public void insertUpdate(DocumentEvent e) {
	    	processUpdate(e);	
	    }
	    
	    public void removeUpdate(DocumentEvent e) {
	    	processUpdate(e);
	    }
	    
	    public void changedUpdate(DocumentEvent e) {
	    	processUpdate(e);
	    }
	    
	    private void processUpdate(DocumentEvent e) {
	    	var document = e.getDocument();	    	
	    	try {
				this.consumer.accept(document.getText(0, document.getLength()));
			} catch (Exception ex) {
				Logger.Log(ex);
			}
	    }
	}
}
