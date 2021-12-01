package de.lordz.java.tools.tdm;

import java.awt.Component;
import java.awt.Color;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import com.google.common.base.Strings;

public class CustomerBasicInfo extends JPanel {

	private static final long serialVersionUID = 5327206289101137863L;
	private JTextField textFieldName;
	private JLabel labelStreet;
	private JLabel labelPostcode;
	private JTextField textFieldStreet;
	private JTextField textFieldPostcode;
	private JLabel labelCity;
	private JTextField textFieldCity;
	private JTextField textFieldDistance;
	private JTextArea textAreaDescription;
	private CustomerEntity currentCustomer;
	private boolean editMode;
		
	public CustomerBasicInfo() {
		var sl_contentPanel = new SpringLayout();
		setLayout(sl_contentPanel);		
		JLabel labelName = new JLabel(LocalizationProvider.GetString("customerdialog.label.name") + ":");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, labelName, 5, SpringLayout.NORTH, this);
		sl_contentPanel.putConstraint(SpringLayout.WEST, labelName, 5, SpringLayout.WEST, this);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, labelName, 31, SpringLayout.NORTH, this);
		sl_contentPanel.putConstraint(SpringLayout.EAST, labelName, 89, SpringLayout.WEST, this);
		add(labelName);
		{
			textFieldName = new JTextField();
			sl_contentPanel.putConstraint(SpringLayout.NORTH, textFieldName, 2, SpringLayout.NORTH, labelName);
			sl_contentPanel.putConstraint(SpringLayout.WEST, textFieldName, 6, SpringLayout.EAST, labelName);
			sl_contentPanel.putConstraint(SpringLayout.EAST, textFieldName, 217, SpringLayout.WEST, this);
			add(textFieldName);
		}
		{
			labelStreet = new JLabel(LocalizationProvider.GetString("customerdialog.label.street") + ":");
			sl_contentPanel.putConstraint(SpringLayout.WEST, labelStreet, 0, SpringLayout.WEST, labelName);
			add(labelStreet);
		}
		{
			textFieldStreet = new JTextField();
			sl_contentPanel.putConstraint(SpringLayout.NORTH, labelStreet, 3, SpringLayout.NORTH, textFieldStreet);
			sl_contentPanel.putConstraint(SpringLayout.NORTH, textFieldStreet, 6, SpringLayout.SOUTH, textFieldName);
			sl_contentPanel.putConstraint(SpringLayout.WEST, textFieldStreet, 0, SpringLayout.WEST, textFieldName);
			sl_contentPanel.putConstraint(SpringLayout.EAST, textFieldStreet, 0, SpringLayout.EAST, textFieldName);
			add(textFieldStreet);
		}
		{
			labelPostcode = new JLabel(LocalizationProvider.GetString("customerdialog.label.postcode") + ":");
			sl_contentPanel.putConstraint(SpringLayout.WEST, labelPostcode, 0, SpringLayout.WEST, labelName);
			add(labelPostcode);
		}
		{
			textFieldPostcode = new JTextField();
			sl_contentPanel.putConstraint(SpringLayout.NORTH, labelPostcode, 3, SpringLayout.NORTH, textFieldPostcode);
			sl_contentPanel.putConstraint(SpringLayout.NORTH, textFieldPostcode, 6, SpringLayout.SOUTH, textFieldStreet);
			sl_contentPanel.putConstraint(SpringLayout.WEST, textFieldPostcode, 0, SpringLayout.WEST, textFieldName);
			sl_contentPanel.putConstraint(SpringLayout.EAST, textFieldPostcode, 0, SpringLayout.EAST, textFieldName);
			add(textFieldPostcode);
		}
		{
			labelCity = new JLabel(LocalizationProvider.GetString("customerdialog.label.city") + ":");
			sl_contentPanel.putConstraint(SpringLayout.WEST, labelCity, 0, SpringLayout.WEST, labelName);
			add(labelCity);
		}
		
		textFieldCity = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, labelCity, 3, SpringLayout.NORTH, textFieldCity);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textFieldCity, 6, SpringLayout.SOUTH, textFieldPostcode);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textFieldCity, 0, SpringLayout.WEST, textFieldName);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textFieldCity, 0, SpringLayout.EAST, textFieldName);
		add(textFieldCity);
		textFieldCity.setColumns(10);
		
		textFieldDistance = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textFieldDistance, 6, SpringLayout.SOUTH, textFieldCity);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textFieldDistance, 0, SpringLayout.WEST, textFieldName);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textFieldDistance, 0, SpringLayout.EAST, textFieldName);
		textFieldDistance.setColumns(10);
		add(textFieldDistance);
		
		JLabel labelDistance = new JLabel(LocalizationProvider.GetString("customerdialog.label.distance") + ":");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, labelDistance, 3, SpringLayout.NORTH, textFieldDistance);
		sl_contentPanel.putConstraint(SpringLayout.WEST, labelDistance, 0, SpringLayout.WEST, labelName);
		add(labelDistance);
		
		JLabel labelDescription = new JLabel(LocalizationProvider.GetString("customerdialog.label.description") + ":");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, labelDescription, 14, SpringLayout.SOUTH, labelDistance);
		sl_contentPanel.putConstraint(SpringLayout.WEST, labelDescription, 0, SpringLayout.WEST, labelName);
		add(labelDescription);
		
		textAreaDescription = new JTextArea();
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, textAreaDescription, -87, SpringLayout.SOUTH, this);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textAreaDescription, 192, SpringLayout.EAST, textFieldDistance);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textAreaDescription, 6, SpringLayout.SOUTH, textFieldDistance);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textAreaDescription, 0, SpringLayout.WEST, textFieldDistance);
		textAreaDescription.setColumns(10);
		add(textAreaDescription);
	}
	
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
	
	public void setEditable(boolean editable) {
		this.editMode = editable;
		var border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
		for (Component item : getComponents()) {
			if (item != null && item instanceof JTextComponent) {
				var textComponent = (JTextComponent)item;
				textComponent.setEditable(editable);
				if (textComponent instanceof JTextArea) {
					textComponent.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(2, 2, 2, 2)));
				}
			}
		}
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
