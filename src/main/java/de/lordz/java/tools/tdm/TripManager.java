package de.lordz.java.tools.tdm;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

import de.lordz.java.tools.tdm.common.DateTimeHelper;
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
                final var parameters = new ArrayList<AbstractMap.SimpleEntry<String, Object>>(1);
                parameters.add(new AbstractMap.SimpleEntry<String, Object>("tripId", id));
                result = DatabaseProvider.getEntity("SELECT t FROM Trip c WHERE t.deleted=0 AND t.id=:tripId",
                        Trip.class, parameters);
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
        return getTrips(null, null);
    }
    
    /**
     * Gets all available trips.
     * 
     * @param start The selection start date if specified.
     * @param end The selection end date if specified.
     * @return Returns a list of trips on success, otherwise null.
     */
    public static List<Trip> getTrips(LocalDate start, LocalDate end) {
        return getTrips(start, end, true);
    }
    
    /**
     * Gets all available trips.
     * 
     * @param start The selection start date if specified.
     * @param end The selection end date if specified.
     * @param sortDesceding If true the selection is sorted descending by date and identifier.
     * @return Returns a list of trips on success, otherwise empty list.
     */
    public static List<Trip> getTrips(LocalDate start, LocalDate end, boolean sortDesceding) {
        List<Trip> result = null;
        try {
            if (DatabaseProvider.getIsOpen()) {
                final var queryTemplate = "SELECT t FROM Trip t WHERE t.deleted=0 %s ORDER BY %s";
                final var orderBy = sortDesceding ? "t.timeOfTrip DESC, t.id DESC" : "t.timeOfTrip ASC, t.id ASC";
                String query = null;
                final var parameters = new ArrayList<AbstractMap.SimpleEntry<String, Object>>(2);
                if (start == null && end == null) {
                    query = String.format(queryTemplate, "", orderBy);
                } else {
                    String whereClause = "";
                    if (start != null) {
                        whereClause = " AND t.timeOfTrip >= :startDate";
                        parameters.add(new AbstractMap.SimpleEntry<String, Object>("startDate", DateTimeHelper.toSortableDateTime(start)));
                    }
                    
                    if (end != null) {
                        if (!Strings.isNullOrEmpty(whereClause)) {
                            whereClause += " ";
                        }
                        
                        whereClause += " AND t.timeOfTrip <= :endDate";
                        parameters.add(new AbstractMap.SimpleEntry<String, Object>("endDate", DateTimeHelper.toSortableDateTime(end)));
                    }
                    
                    query = String.format(queryTemplate, whereClause, orderBy);
                }
                
                if (!Strings.isNullOrEmpty(query)) {
                    result = DatabaseProvider.getEntities(query, Trip.class, parameters);
                }
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
     * @return Returns a list of trip types on success, otherwise empty list.
     */
    public static List<TripType> getTripTypes() {
        List<TripType> result = null;
        try {
            if (DatabaseProvider.getIsOpen()) {
                result = DatabaseProvider.getEntities("SELECT t FROM TripType t WHERE t.deleted=0",
                        TripType.class, null);
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        if (result == null) {
            result = new ArrayList<TripType>(0);
        }
        
        return result;
    }
    
    /**
     * Gets all available travel allowances
     * 
     * @return Returns a list of travel allowances on success, otherwise empty list.
     */
    public static List<TravelAllowance> getTravelAllowances() {
        List<TravelAllowance> result = null;
        try {
            if (DatabaseProvider.getIsOpen()) {
                result = DatabaseProvider.getEntities("SELECT t FROM TravelAllowance t WHERE t.deleted=0", TravelAllowance.class, null);
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        if (result == null) {
            result = new ArrayList<TravelAllowance>(0);
        }
        
        return result;
    }
}
