package de.lordz.java.tools.tdm;

import java.util.AbstractMap;
import java.util.List;

import de.lordz.java.tools.tdm.common.Logger;
import de.lordz.java.tools.tdm.entities.TripEntity;

/**
 * Class to manage trips.
 * 
 * @author lordz
 *
 */
public class TripManager {

    /**
     * Gets the specified trip.
     * 
     * @param id The identifier of the trip.
     * @return The customer entity object on success, otherwise null.
     */
    public static TripEntity getTrip(int id) {
        TripEntity result = null;
        try {
            if (DatabaseProvider.getIsOpen()) {
                var parameter = new AbstractMap.SimpleEntry<String, Object>("tripId", id);
                result = DatabaseProvider.getEntity("SELECT t FROM TripEntity c WHERE t.id=:tripId",
                        TripEntity.class, parameter);
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
    public static List<TripEntity> getTrips() {
        List<TripEntity> result = null;
        try {
            if (DatabaseProvider.getIsOpen()) {
                result = DatabaseProvider.getEntities("SELECT t FROM TripEntity t WHERE t.deleted=0",
                        TripEntity.class);
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        return result;
    }
}
