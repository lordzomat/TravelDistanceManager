package de.lordz.java.tools.tdm;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import de.lordz.java.tools.tdm.common.Logger;
import de.lordz.java.tools.tdm.entities.*;

/**
 * Class to manage trips.
 * 
 * @author lordzomat
 *
 */
public class TripManager {

    /**
     * Gets the specified trip.
     * 
     * @param id The identifier of the trip.
     * @return The customer entity object on success, otherwise null.
     */
    public static Trip getTrip(int id) {
        Trip result = null;
        try {
            if (DatabaseProvider.getIsOpen()) {
                var parameter = new AbstractMap.SimpleEntry<String, Object>("tripId", id);
                result = DatabaseProvider.getEntity("SELECT t FROM Trip c WHERE t.deleted=0 AND t.id=:tripId",
                        Trip.class, parameter);
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        return result;
    }

    /**
     * Gets all available trips.
     * 
     * @return Returns a list of trips on success, otherwise null.
     */
    public static List<Trip> getTrips() {
        List<Trip> result = null;
        try {
            if (DatabaseProvider.getIsOpen()) {
                result = DatabaseProvider.getEntities("SELECT t FROM Trip t WHERE t.deleted=0 ORDER BY t.timeOfTrip DESC, t.id DESC",
                        Trip.class);
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        if (result == null) {
            result = new ArrayList<Trip>(0);
        }
        
        return result;
    }
    
    /**
     * Checks if the specified customer is assigned to any active trip.
     * 
     * @param tripTypeId The customer.
     * @return Returns true if the customer is assigned or the check failed, otherwise false.
     */
    public static boolean checkIsCustomerAssignd(int customerId) {
        boolean result = true;
        try {
            var singleResult = DatabaseProvider
                    .getSqlSingleResult("SELECT EXISTS (SELECT 1 FROM tbTrip WHERE coDeleted=0 AND coCustomerId=?1)", customerId);
            if (singleResult != null && singleResult instanceof Integer) {
                result = (int) singleResult == 1;
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        return result;
    }
    
    /**
     * Checks if the specified trip type is assigned to any active trip.
     * 
     * @param tripTypeId The trip type.
     * @return Returns true if the trip type is assigned or the check failed, otherwise false.
     */
    public static boolean checkIsTripTypeAssigned(int tripTypeId) {
        boolean result = true;
        try {
            var singleResult = DatabaseProvider
                    .getSqlSingleResult("SELECT EXISTS (SELECT 1 FROM tbTrip WHERE coDeleted=0 AND coTripTypeId=?1)", tripTypeId);
            if (singleResult != null && singleResult instanceof Integer) {
                result = (int) singleResult == 1;
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        return result;        
    }
    
    /**
     * Gets all available trip types.
     * 
     * @return Returns a list of trip types on success, otherwise null.
     */
    public static List<TripType> getTripTypes() {
        List<TripType> result = null;
        try {
            if (DatabaseProvider.getIsOpen()) {
                result = DatabaseProvider.getEntities("SELECT t FROM TripType t WHERE t.deleted=0",
                        TripType.class);
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        if (result == null) {
            result = new ArrayList<TripType>(0);
        }
        
        return result;
    }
}
