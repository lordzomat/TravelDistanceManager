package de.lordz.java.tools.tdm;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import de.lordz.java.tools.tdm.common.Logger;
import de.lordz.java.tools.tdm.entities.Customer;

/**
 * Class to manage customers.
 * 
 * @author lordzomat
 *
 */
public class CustomerManager {

    /**
     * Gets the specified customer.
     * 
     * @param id The identifier of the customer.
     * @return The customer entity object on success, otherwise null.
     */
    public static Customer getCustomer(int id) {
        Customer result = null;
        try {
            if (DatabaseProvider.getIsOpen()) {
                var parameters = new ArrayList<AbstractMap.SimpleEntry<String, Object>>(1);
                parameters.add(new AbstractMap.SimpleEntry<String, Object>("customerId", id));
                result = DatabaseProvider.getEntity("SELECT c FROM Customer c WHERE c.deleted=0 AND c.id=:customerId", Customer.class,
                        parameters);
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        return result;
    }

    /**
     * Gets all available customers.
     * 
     * @return Returns a list of customers on success, otherwise null.
     */
    public static List<Customer> getCustomers() {
        List<Customer> result = null;
        try {
            if (DatabaseProvider.getIsOpen()) {
                result = DatabaseProvider.getEntities("SELECT c FROM Customer c WHERE c.deleted=0", Customer.class, null);
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        if (result == null) {
            result = new ArrayList<Customer>(0);
        }
        
        return result;
    }
}
