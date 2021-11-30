package de.lordz.java.tools.tdm;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.google.common.base.Strings;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import javax.swing.JTextArea;

public class CustomerDialog extends JDialog {

	private static final long serialVersionUID = -3335942655314531982L;
	private final JPanel contentPanel = new JPanel();
	private JButton buttonOk;
	private JTextField textFieldName;
	private JLabel labelStreet;
	private JLabel labelPostcode;
	private JTextField textFieldStreet;
	private JTextField textFieldPostcode;
	private CustomerEntity currentCustomer;
	private JLabel labelCity;
	private JTextField textFieldCity;
	private JTextField textFieldDistance;
	private JTextArea textAreaDescription;

	/**
	 * Create the dialog.
	 */
	public CustomerDialog() {
		setBounds(100, 100, 450, 300);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		JLabel labelName = new JLabel(LocalizationProvider.GetString("customerdialog.label.name") + ":");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, labelName, 5, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, labelName, 5, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, labelName, 31, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, labelName, 89, SpringLayout.WEST, contentPanel);
		contentPanel.add(labelName);
		{
			textFieldName = new JTextField();
			sl_contentPanel.putConstraint(SpringLayout.NORTH, textFieldName, 2, SpringLayout.NORTH, labelName);
			sl_contentPanel.putConstraint(SpringLayout.WEST, textFieldName, 6, SpringLayout.EAST, labelName);
			sl_contentPanel.putConstraint(SpringLayout.EAST, textFieldName, 217, SpringLayout.WEST, contentPanel);
			contentPanel.add(textFieldName);
		}
		{
			labelStreet = new JLabel(LocalizationProvider.GetString("customerdialog.label.street") + ":");
			sl_contentPanel.putConstraint(SpringLayout.WEST, labelStreet, 0, SpringLayout.WEST, labelName);
			contentPanel.add(labelStreet);
		}
		{
			textFieldStreet = new JTextField();
			sl_contentPanel.putConstraint(SpringLayout.NORTH, labelStreet, 3, SpringLayout.NORTH, textFieldStreet);
			sl_contentPanel.putConstraint(SpringLayout.NORTH, textFieldStreet, 6, SpringLayout.SOUTH, textFieldName);
			sl_contentPanel.putConstraint(SpringLayout.WEST, textFieldStreet, 0, SpringLayout.WEST, textFieldName);
			sl_contentPanel.putConstraint(SpringLayout.EAST, textFieldStreet, 0, SpringLayout.EAST, textFieldName);
			contentPanel.add(textFieldStreet);
		}
		{
			labelPostcode = new JLabel(LocalizationProvider.GetString("customerdialog.label.postcode") + ":");
			sl_contentPanel.putConstraint(SpringLayout.WEST, labelPostcode, 0, SpringLayout.WEST, labelName);
			contentPanel.add(labelPostcode);
		}
		{
			textFieldPostcode = new JTextField();
			sl_contentPanel.putConstraint(SpringLayout.NORTH, labelPostcode, 3, SpringLayout.NORTH, textFieldPostcode);
			sl_contentPanel.putConstraint(SpringLayout.NORTH, textFieldPostcode, 6, SpringLayout.SOUTH, textFieldStreet);
			sl_contentPanel.putConstraint(SpringLayout.WEST, textFieldPostcode, 0, SpringLayout.WEST, textFieldName);
			sl_contentPanel.putConstraint(SpringLayout.EAST, textFieldPostcode, 0, SpringLayout.EAST, textFieldName);
			contentPanel.add(textFieldPostcode);
		}
		{
			labelCity = new JLabel(LocalizationProvider.GetString("customerdialog.label.city") + ":");
			sl_contentPanel.putConstraint(SpringLayout.WEST, labelCity, 0, SpringLayout.WEST, labelName);
			contentPanel.add(labelCity);
		}
		
		textFieldCity = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, labelCity, 3, SpringLayout.NORTH, textFieldCity);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textFieldCity, 6, SpringLayout.SOUTH, textFieldPostcode);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textFieldCity, 0, SpringLayout.WEST, textFieldName);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textFieldCity, 0, SpringLayout.EAST, textFieldName);
		contentPanel.add(textFieldCity);
		textFieldCity.setColumns(10);
		
		textFieldDistance = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textFieldDistance, 6, SpringLayout.SOUTH, textFieldCity);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textFieldDistance, 0, SpringLayout.WEST, textFieldName);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textFieldDistance, 0, SpringLayout.EAST, textFieldName);
		textFieldDistance.setColumns(10);
		contentPanel.add(textFieldDistance);
		
		JLabel labelDistance = new JLabel(LocalizationProvider.GetString("customerdialog.label.distance") + ":");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, labelDistance, 3, SpringLayout.NORTH, textFieldDistance);
		sl_contentPanel.putConstraint(SpringLayout.WEST, labelDistance, 0, SpringLayout.WEST, labelName);
		contentPanel.add(labelDistance);
		
		JLabel labelDescription = new JLabel(LocalizationProvider.GetString("customerdialog.label.description") + ":");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, labelDescription, 14, SpringLayout.SOUTH, labelDistance);
		sl_contentPanel.putConstraint(SpringLayout.WEST, labelDescription, 0, SpringLayout.WEST, labelName);
		contentPanel.add(labelDescription);
		
		textAreaDescription = new JTextArea();
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, textAreaDescription, 0, SpringLayout.SOUTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textAreaDescription, 192, SpringLayout.EAST, textFieldDistance);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textAreaDescription, 6, SpringLayout.SOUTH, textFieldDistance);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textAreaDescription, 0, SpringLayout.WEST, textFieldDistance);
		textAreaDescription.setColumns(10);
		contentPanel.add(textAreaDescription);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				this.buttonOk = new JButton(LocalizationProvider.GetString("mainframe.button.accept"));
				this.buttonOk.setActionCommand("OK");
				this.buttonOk.addActionListener(e -> performButtonClick(e));
				buttonPane.add(this.buttonOk);
				getRootPane().setDefaultButton(this.buttonOk);
			}
			{
				JButton cancelButton = new JButton(LocalizationProvider.GetString("mainframe.button.cancel"));
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(e -> performButtonClick(e));
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public void showDialog(CustomerEntity customer, java.awt.Window window) {
		try {
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
			
			this.currentCustomer = customer;
			this.textFieldName.getDocument().addDocumentListener(new EntityTextChangeDocumentListener((value) -> this.currentCustomer.setName(value)));
			this.textFieldStreet.getDocument().addDocumentListener(new EntityTextChangeDocumentListener((value) -> this.currentCustomer.setStreet(value)));				
			this.textFieldPostcode.getDocument().addDocumentListener(new EntityTextChangeDocumentListener((value) -> this.currentCustomer.setPostcode(value)));
			this.textFieldCity.getDocument().addDocumentListener(new EntityTextChangeDocumentListener((value) -> this.currentCustomer.setCity(value)));				
			this.textFieldDistance.getDocument().addDocumentListener(new EntityTextChangeDocumentListener((value) -> UpdateDistance(value)));
			this.textAreaDescription.getDocument().addDocumentListener(new EntityTextChangeDocumentListener((value) -> this.currentCustomer.setDescription(value)));
		}
		catch (Exception ex) {
			Logger.Log(ex);
		}
		
		String titleKey = customer == null ? "customerdialog.title.new" : "customerdialog.title.edit";
		this.setTitle(LocalizationProvider.GetString(titleKey));
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		this.setLocationRelativeTo(window);
		this.setVisible(true);
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

	private void performButtonClick(ActionEvent event) {
		var source = event.getSource();
		if (source != null && source == this.buttonOk) {
			if (this.currentCustomer != null) {
				if (this.currentCustomer.getId() > 0) {
					DatabaseProvider.updateEntity(this.currentCustomer);
				} else {
					DatabaseProvider.saveEntity(this.currentCustomer);
				}
			}
		}
		
		this.setVisible(false);
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
