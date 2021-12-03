package de.lordz.java.tools.tdm;

import java.util.AbstractMap;
import java.util.List;

import de.lordz.java.tools.tdm.common.Logger;

/**
 * Class to manage customers.
 * 
 * @author lordz
 *
 */
public class CustomerManager {
	
	/**
	 * Gets the specified customer.
	 * 
	 * @param id The identifier of the customer. 
	 * @return The customer entity object on success, otherwise null.
	 */
	public static CustomerEntity getCustomer(int id) {
		CustomerEntity result = null;
		try {
			if (DatabaseProvider.getIsOpen()) {
				var parameter = new AbstractMap.SimpleEntry<String, Object>("customerId", id);
				result = DatabaseProvider.getEntity("SELECT c FROM CustomerEntity c WHERE c.id=:customerId", CustomerEntity.class, parameter);
			}
		}
		catch (Exception ex) {
			Logger.Log(ex);
		}
		
		return result;
	}
	
	/**
	 * Gets all available customers.
	 * 
	 * @return Returns a list of customers on success, otherwise null.
	 */
	public static List<CustomerEntity> getCustomers() {
		List<CustomerEntity> result = null;
		try {
			if (DatabaseProvider.getIsOpen()) {
				result = DatabaseProvider.getEntities("SELECT c FROM CustomerEntity c WHERE c.deleted=0", CustomerEntity.class);
			}
		}
		catch (Exception ex) {
			Logger.Log(ex);
		}
		
		return result;
	}
}
