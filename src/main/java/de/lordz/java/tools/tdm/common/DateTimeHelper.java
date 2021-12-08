package de.lordz.java.tools.tdm.common;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateHighlightPolicy;
import com.github.lgooddatepicker.zinternaltools.HighlightInformation;

/**
 * Helper class for date time functionality.
 * 
 * @author lordzomat
 *
 */
public class DateTimeHelper {
    
    /**
     * String representing the minimum date.
     */
    public static final String MinDateString = "0001-01-01";
    
    /**
     * String representing the minimum date time.
     */
    public static final String MinDateTimeString = "0001-01-01T00:00";
    
    /**
     * The date format for display in UI.
     */
    public static final DateTimeFormatter DisplayDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    
    
    /**
     * The day and month date format for display in UI.
     */
    public static final DateTimeFormatter DayMonthDisplayDateFormat = DateTimeFormatter.ofPattern("dd.MM");
    
    /**
     * The short time format for display in UI.
     */
    public static final DateTimeFormatter DisplayShortTimeFormat = DateTimeFormatter.ofPattern("HH:mm");
    
    public static final DateTimeFormatter SortableDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Converts the given LocalDate to display day and month date format string.
     * 
     * @param date The date to convert.
     * @return The formatted string.
     */
    public static String toDayMonthDisplayDateFormat(LocalDate date) {
        if (date != null) {
            return date.format(DayMonthDisplayDateFormat);
        }
        
        return "";
    }
    
    
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
     * Converts the given LocalDate to it's sortable date string representation.
     * 
     * @param date The date to convert.
     * @return The formatted string.
     */
    public static String toSortableDate(LocalDate date) {
        if (date != null) {
            return date.format(SortableDateFormat);
        }
        
        return MinDateString;
    }
    
    /**
     * Converts the given LocalDate to it's sortable date time string representation.
     * 
     * @param date The date to convert.
     * @return The formatted string.
     */
    public static String toSortableDateTime(LocalDate date) {
        if (date != null) {
            return date.format(SortableDateFormat) + "T00:00:00";
        }
        
        return MinDateTimeString;
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
    
    /**
     * Creates a standard date picker.
     * 
     * @return The date picker instance.
     */
    public static DatePicker createDatePicker() {
        var dateSettings = new DatePickerSettings();
        dateSettings.setHighlightPolicy(new WeekendHighlightPolicy());
        dateSettings.setFormatForDatesCommonEra("dd.MM.yyyy");
        return new DatePicker(dateSettings);
    }
    
    private static class WeekendHighlightPolicy implements DateHighlightPolicy {

        @Override
        public HighlightInformation getHighlightInformationOrNull(LocalDate date) {
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                return new HighlightInformation(Color.LIGHT_GRAY, Color.BLACK, null);
            }
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                return new HighlightInformation(Color.GRAY, Color.BLACK, null);
            }
            
            return null;
        }
    }
}
