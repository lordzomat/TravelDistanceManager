package de.lordz.java.tools.tdm.common;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * Helper class for date time functionality.
 * 
 * @author lordzomat
 *
 */
public class DateTimeHelper {
    
    /**
     * The date format for display in UI.
     */
    public static final DateTimeFormatter DisplayDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    
    
    /**
     * The short time format for display in UI.
     */
    public static final DateTimeFormatter DisplayShortTimeFormat = DateTimeFormatter.ofPattern("HH:mm");
    
    /**
     * Converts the given LocalDate to display date format string.
     * 
     * @param date The date to convert.
     * @return The formatted string.
     */
    public static String toDisplayDateFormat(LocalDate date) {
        if (date != null) {
            return date.format(DisplayDateFormat);
        }
        
        return "";
    }
    
    /**
     * Converts the given LocalTime to display short time string.
     * 
     * @param time The time to convert.
     * @return The formatted string.
     */
    public static String toDisplayShortTimeFormat(LocalTime time) {
        if (time != null) {
            return time.format(DisplayShortTimeFormat);
        }
        
        return "";
    }
    
    
    /**
     * Retrieves the current date time in ISO 8601 format including fractions.
     * 
     * @return The ISO 8201 date time string.
     */
    public static String getIsoDateTime() {
        var date = new Date(System.currentTimeMillis());
        var dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return dateFormat.format(date);
    }

    /**
     * Converts a ISO 8601 date time string to a Date object.
     * 
     * @param timestamp The timestamp to convert.
     * @return The Date object.
     */
    public static Date getDateFromIsoDateTime(String timestamp) {
        try {
            String formatToParse = null;
            var colonCount = timestamp.chars().filter(ch -> ch == ':').count();
            if (colonCount == 1) {
                formatToParse = "yyyy-MM-dd'T'HH:mm";
            } else if (colonCount == 2) {
                if (timestamp.indexOf('.') != -1) {
                    formatToParse = "yyyy-MM-dd'T'HH:mm:ss.SSS";
                } else {
                    formatToParse = "yyyy-MM-dd'T'HH:mm:ss";
                }
            }
            
            if (formatToParse !=null) {
                var simpleDateFormat = new SimpleDateFormat(formatToParse);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                return simpleDateFormat.parse(timestamp);
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        return null;
    }
}
