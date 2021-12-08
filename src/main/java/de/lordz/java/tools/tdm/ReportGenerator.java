package de.lordz.java.tools.tdm;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.google.common.base.Strings;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.alignment.VerticalAlignment;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import de.lordz.java.tools.tdm.common.DateTimeHelper;
import de.lordz.java.tools.tdm.common.LocalizationProvider;
import de.lordz.java.tools.tdm.common.Logger;
import de.lordz.java.tools.tdm.entities.*;

/**
 * Class to generate PDF reports.
 * 
 * @author lordzomat
 *
 */
public class ReportGenerator {
    
    public final static int REPORT_SIMPLE_YEARS = 1;
    public final static int REPORT_SIMPLE_MONTHS = 2;
    public final static int REPORT_DETAILED = 3;
    
    final private static DecimalFormat distanceFormat = new DecimalFormat("0.00");
    final private static int columnCountYears = 14;
    final private static int columnCountMonths = 2;
    final private static int columnCountDetailed = 5;
    final private static Font boldFont = createBoldFont();
    final private static GrayColor headerColor = new GrayColor(0.8f);
    final private Rectangle rectangle;
    final private HashMap<Integer, Customer> customers;
    final private HashMap<Integer, TripType> tripTypes;
    final private Locale locale;
    
    /**
     * Initializes a new report generator class.
     * 
     * @param customers A hash map to lookup customers.
     * @param tripTypes A hash map to lookup trip types.
     */
    public ReportGenerator(HashMap<Integer, Customer> customers, HashMap<Integer, TripType> tripTypes) {
        this.rectangle = PageSize.A4;
        this.customers = customers;
        this.tripTypes = tripTypes;
        this.locale = LocalizationProvider.getLocale();
    }
    
    /**
     * Generates the specified report kind.
     * 
     * @param reportKind The kind of the report to generate.
     * @param year The (starting) year for which the report has to be generated.
     * @param outputFilePath The output file path of the report.
     * @return Returns <CODE>true</CODE> if the report was created, otherwise <CODE>false</CODE>.
     */
    public boolean generateReport(int reportKind, int year, Path outputFilePath) {
        boolean result = false;
        try {
            if (reportKind < REPORT_SIMPLE_YEARS || reportKind > REPORT_DETAILED) {
                return false;
            }
            
            switch (reportKind) {
                case REPORT_SIMPLE_YEARS:
                    result = createSimpleYearsReport(year, outputFilePath);
                    break;
                case REPORT_SIMPLE_MONTHS:
                    result = createSimpleMonthsReport(year, outputFilePath);
                    break;
                case REPORT_DETAILED:
                    result = createDetailedReport(year, outputFilePath);
                    break;
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }
                
        return result;
    }
    
    private boolean createSimpleYearsReport(int year, Path outputFilePath) {
        boolean result = false;
        Document document = null;
        try {
            if (outputFilePath != null) {
                final var outputFile = outputFilePath.toFile();
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                
                document = new Document(this.rectangle.rotate(), 30, 30, 30, 30);
                @SuppressWarnings("unused")
                final var writer = PdfWriter.getInstance(document, new FileOutputStream(outputFilePath.toString()));
                document.open();
                
                PdfPTable table = null;
                final var startYear = year - 5;
                final var trips = TripManager.getTrips(LocalDate.of(startYear, 1, 1), LocalDate.of(year, 12, 31));
                if (trips != null && trips.size() > 0) {
                    table = new PdfPTable(columnCountYears);
                    table.setWidthPercentage(100);
                    table.setHeaderRows(1);
                    addCell(table, createBoldPhrase(LocalizationProvider.getString("report.label.year")), e -> setHeaderBackgroundColor(e));
                    for (var month : Month.values()) {
                        addCell(table, createBoldPhrase(month.getDisplayName(TextStyle.SHORT, this.locale)), HorizontalAlignment.CENTER,
                                e -> setHeaderBackgroundColor(e));
                    }
                    addCell(table, createBoldPhrase(LocalizationProvider.getString("report.label.total")), HorizontalAlignment.RIGHT,
                            e -> setHeaderBackgroundColor(e));
                    
                    final var yearMap = createYearMap(trips);
                    double overallTotal = 0.0;
                    double monthTotal;
                    double totalTravelExpenses = 0.0;
                    for (var key : yearMap.keySet()) {
                        monthTotal = 0.0;
                        addCell(table, createBoldPhrase(String.valueOf(key)), e -> setHeaderBackgroundColor(e));
                        final var yearInfo = yearMap.get(key);
                        final var distances = yearInfo.Distances;
                        for (var monthDistance : distances) {
                            monthTotal += monthDistance;
                            addCell(table, distanceFormat.format(monthDistance), HorizontalAlignment.RIGHT);
                        }
                        addCell(table, createBoldPhrase(distanceFormat.format(monthTotal)), HorizontalAlignment.RIGHT);
                        overallTotal += monthTotal;
                        totalTravelExpenses += yearInfo.getTravelExpenses();
                    }

                    addCell(table, createBoldPhrase(LocalizationProvider.getString("report.label.total")), e -> e.setColspan(columnCountYears - 1));
                    addCell(table, createBoldPhrase(distanceFormat.format(overallTotal)), HorizontalAlignment.RIGHT);
                    
                    addCell(table, createBoldPhrase(LocalizationProvider.getString("report.label.rate")), e -> e.setColspan(columnCountYears - 1));
                    addCell(table, createBoldPhrase(distanceFormat.format(totalTravelExpenses)), HorizontalAlignment.RIGHT);
                }
                
                if (table != null) {
                    document.add(table);
                } else {
                    document.add(new Paragraph(LocalizationProvider.getString("report.nodata")));
                }
                
                document.close();
                
                result = true;
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }
        
        if (!result) {
            document.close();
            final var outputFile = outputFilePath.toFile();
            if (outputFile.exists()) {
                outputFile.delete();
            }
        }
        
        return result;
    }
        
    private boolean createSimpleMonthsReport(int year, Path outputFilePath) {
        boolean result = false;
        Document document = null;
        try {
            if (outputFilePath != null) {
                var outputFile = outputFilePath.toFile();
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                
                document = new Document(this.rectangle, 30, 30, 30, 30);
                @SuppressWarnings("unused")
                final var writer = PdfWriter.getInstance(document, new FileOutputStream(outputFilePath.toString()));
                document.open();
                              
                final var tables = new ArrayList<PdfPTable>();
                final var startYear = year - 5;
                final var trips = TripManager.getTrips(LocalDate.of(startYear, 1, 1), LocalDate.of(year, 12, 31));
                if (trips != null && trips.size() > 0) {
                    final var yearMap = createYearMap(trips);
                    int tableCount = 0;
                    for (var key : yearMap.keySet()) {
                        var value = yearMap.get(key);
                        var table = createYearTable(key, value);
                        if (table != null) {
                            if (tableCount != 0) {
                                table.setSpacingBefore(50);
                            }
                            
                            tables .add(table);                            
                            tableCount++;
                        }
                    }
                }
                
                if (tables.size() > 0) {
                  for (var table : tables) {
                      document.add(table);
                  }  
                } else {
                    document.add(new Paragraph(LocalizationProvider.getString("report.nodata")));
                }
                
                document.close();
                result = true;
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }
        
        if (!result) {
            document.close();
            final var outputFile = outputFilePath.toFile();
            if (outputFile.exists()) {
                outputFile.delete();
            }
        }
        
        return result;
    }
    
    private PdfPTable createYearTable(int year, YearInfo yearInfo) {
        PdfPTable result = null;
        if (yearInfo == null) {
            return result;
        }
        
        try {
            final var table = new PdfPTable(columnCountMonths);
            table.setWidthPercentage(50);
            table.setHeaderRows(1);
            addCell(table, createBoldPhrase(String.valueOf(year)), HorizontalAlignment.CENTER,
                    e -> {
                        setHeaderBackgroundColor(e);
                        e.setColspan(columnCountMonths);
                    });
            addCell(table, createBoldPhrase(LocalizationProvider.getString("report.column.header.month")),
                    e -> setHeaderBackgroundColor(e));
            addCell(table, createBoldPhrase(LocalizationProvider.getString("report.column.header.distance")),
                    HorizontalAlignment.RIGHT, e -> setHeaderBackgroundColor(e));

            double overallTotal = 0.0;
            final var distances = yearInfo.Distances;
            for (int i = 0; i < distances.size(); i++) {
                var monthDistance = distances.get(i);
                overallTotal += monthDistance;
                addCell(table, Month.of(i + 1).getDisplayName(TextStyle.FULL, this.locale));
                addCell(table, distanceFormat.format(monthDistance), HorizontalAlignment.RIGHT);
            }

            addColspanRow(table, null, columnCountMonths);
            addCell(table, createBoldPhrase(LocalizationProvider.getString("report.label.total")));
            addCell(table, createBoldPhrase(distanceFormat.format(overallTotal)), HorizontalAlignment.RIGHT);
            
            addCell(table, createBoldPhrase(LocalizationProvider.getString("report.label.rate")));
            addCell(table, createBoldPhrase(distanceFormat.format(yearInfo.getTravelExpenses())), HorizontalAlignment.RIGHT);
            result = table;
        } catch (Exception ex) {
            Logger.Log(ex);
        }
        return result;
    }
    
    private boolean createDetailedReport(int year, Path outputFilePath) {
        boolean result = false;
        Document document = null;
        try {
            if (outputFilePath != null) {
                final var outputFile = outputFilePath.toFile();
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                
                document = new Document(this.rectangle, 30, 30, 30, 30);
                @SuppressWarnings("unused")
                final var writer = PdfWriter.getInstance(document, new FileOutputStream(outputFilePath.toString()));
                document.open();
                
                PdfPTable table = null;
                final var trips = TripManager.getTrips(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31), false);
                if (trips != null && trips.size() > 0) {
                    table = new PdfPTable(columnCountDetailed);
                    table.setWidthPercentage(100);
                    table.setWidths(new int[] { 10, 40, 20, 20, 10 });
                    addHeader(table, boldFont, year);
                    int currentMonth = -1;
                    double monthDistance = 0.0;
                    double totalDistance = 0.0;
                    double totalTravelExpenses = 0.0;
                    for (var trip : trips) {
                        final var tripDate = trip.getLocalDate();
                        final var month = tripDate.getMonthValue();
                        if (currentMonth != month || currentMonth == -1) {                            
                            if (currentMonth != -1) {
                                addDistanceSum(table, LocalizationProvider.getString("report.label.totalmonth"), monthDistance);
                            }
                                                       
                            addColspanRow(table, null, columnCountDetailed);
                            addColspanRow(table,
                                    trip.getLocalDate().getMonth().getDisplayName(TextStyle.FULL, this.locale),
                                    columnCountDetailed);
                            currentMonth = month;
                            monthDistance = 0;
                        }
                        

                        final var customer = this.customers.get(trip.getCustomerId());
                        final var tripType = this.tripTypes.get(trip.getTripTypeId());
                        final var distance = customer.getDistance();
                        monthDistance += distance;
                        totalDistance += distance;
                        totalTravelExpenses += distance * getTravelAllowanceRate(tripDate);
                        addCell(table, DateTimeHelper.toDayMonthDisplayDateFormat(trip.getLocalDate()));
                        addCell(table, customer.getName());
                        addCell(table, customer.getCity());
                        addCell(table, tripType.getName());
                        addCell(table, Double.toString(distance), HorizontalAlignment.RIGHT);
                    }
                    
                    if (currentMonth != -1) {
                        addDistanceSum(table, LocalizationProvider.getString("report.label.totalmonth"), monthDistance);
                        addColspanRow(table, null, columnCountDetailed);
                        addDistanceSum(table, LocalizationProvider.getString("report.label.totalyear"), totalDistance);
                        addDistanceSum(table, LocalizationProvider.getString("report.label.rate"), totalTravelExpenses);
                    }
                }
                
                if (table != null) {
                    document.add(table);
                } else {
                    document.add(new Paragraph(LocalizationProvider.getString("report.nodata")));
                }
                
                document.close();
                
                result = true;
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }
        
        if (!result) {
            document.close();
            final var outputFile = outputFilePath.toFile();
            if (outputFile.exists()) {
                outputFile.delete();
            }
        }
        
        return result;
    }
    
    private TreeMap<Integer, YearInfo> createYearMap(List<Trip> trips) {
        final var map = new TreeMap<Integer, YearInfo>();
        for(var trip : trips) {
            final var customer = this.customers.get(trip.getCustomerId());
            if (customer != null) {
                var tripDate = trip.getLocalDate();
                int tripYear = tripDate.getYear();
                var yearInfo = map.get(tripYear);
                if (yearInfo == null) {
                    yearInfo = new YearInfo();
                    map.put(tripYear, yearInfo);
                }
                
                final var tripMonth = tripDate.getMonthValue() - 1;
                final var distances = yearInfo.Distances;
                var monthDistance = distances.get(tripMonth);
                var distance = customer.getDistance();
                yearInfo.addTravelExpense(distance * getTravelAllowanceRate(tripDate));
                monthDistance += distance;
                distances.set(tripMonth, monthDistance);
            }
        }
        
        return map;
    }
    

    
    private double getTravelAllowanceRate(LocalDate date) {
        return 0.30;
    }
    
    private void addDistanceSum(PdfPTable table, String title, double distance) {
        addCell(table, createBoldPhrase(title), e -> e.setColspan(3));
        addCell(table, createBoldPhrase(distanceFormat.format(distance)), HorizontalAlignment.RIGHT, e -> e.setColspan(2));
    }
    
    private void addColspanRow(PdfPTable table, String text, int columnCount) {
        var cell = new PdfPCell();
        cell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM);
        cell.setColspan(columnCount);
        if (!Strings.isNullOrEmpty(text)) {
            cell.setPhrase(createBoldPhrase(text));
        } else {
            cell.setFixedHeight(20);            
        }
        
        table.addCell(cell);
    }
    
    private static Phrase createBoldPhrase(String text) {
        return new Phrase(text, boldFont);
    }

    private static void addCell(PdfPTable table, String text) {
        addCell(table, new Phrase(text), (Consumer<PdfPCell>)null);
    }
    
//    private static void addCell(PdfPTable table, String text, Consumer<PdfPCell> cellConsumer) {
//        addCell(table, new Phrase(text), HorizontalAlignment.LEFT, cellConsumer);
//    }
    
    private static void addCell(PdfPTable table, Phrase phrase) {
        addCell(table, phrase, (Consumer<PdfPCell>)null);
    }
    
    private static void addCell(PdfPTable table, Phrase phrase, Consumer<PdfPCell> cellConsumer) {
        addCell(table, phrase, HorizontalAlignment.LEFT, cellConsumer);
    }
     
    private static void addCell(PdfPTable table, String text, HorizontalAlignment horizontalAlignment) {
        addCell(table, new Phrase(text), horizontalAlignment, null);
    }
    
    private static void addCell(PdfPTable table, Phrase phrase, HorizontalAlignment horizontalAlignment) {
        addCell(table, phrase, horizontalAlignment, null);
    }
    
    private static void addCell(PdfPTable table, Phrase phrase, HorizontalAlignment horizontalAlignment, Consumer<PdfPCell> cellConsumer) {
        var cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(horizontalAlignment.getId());
        if (cellConsumer != null) {
            cellConsumer.accept(cell);
        }
        
        table.addCell(cell);
    }
       
    private static void addHeader(PdfPTable table, Font font, int year) {
        var color = new GrayColor(0.8f);
        table.setHeaderRows(2);
        addCell(table, createBoldPhrase(String.valueOf(year)), HorizontalAlignment.CENTER, e -> {
            e.setColspan(columnCountDetailed);
            e.setBackgroundColor(color);            
        });
        
        var cell = new PdfPCell(new Paragraph(LocalizationProvider.getString("report.column.header.date"), font));
        cell.setBackgroundColor(color);
        cell.setHorizontalAlignment(HorizontalAlignment.CENTER.getId());
        cell.setVerticalAlignment(VerticalAlignment.CENTER.getId());
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(LocalizationProvider.getString("report.column.header.customer"), font));
        cell.setBackgroundColor(color);
        cell.setHorizontalAlignment(HorizontalAlignment.CENTER.getId());
        cell.setVerticalAlignment(VerticalAlignment.CENTER.getId());
        table.addCell(cell);
        
        cell = new PdfPCell(new Paragraph(LocalizationProvider.getString("report.column.header.location"), font));
        cell.setBackgroundColor(color);
        cell.setHorizontalAlignment(HorizontalAlignment.CENTER.getId());
        cell.setVerticalAlignment(VerticalAlignment.CENTER.getId());
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(LocalizationProvider.getString("report.column.header.triptype"), font));
        cell.setBackgroundColor(color);
        cell.setHorizontalAlignment(HorizontalAlignment.CENTER.getId());
        cell.setVerticalAlignment(VerticalAlignment.CENTER.getId());
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(LocalizationProvider.getString("report.column.header.distance"), font));
        cell.setBackgroundColor(color);
        cell.setHorizontalAlignment(HorizontalAlignment.CENTER.getId());
        cell.setVerticalAlignment(VerticalAlignment.CENTER.getId());
        table.addCell(cell);
    }
    
    private static void setHeaderBackgroundColor(PdfPCell cell) {
        cell.setBackgroundColor(headerColor);
    }
    
    private static Font createBoldFont() {
        var boldFont = new Font();
        boldFont.setStyle(Font.BOLD);
        return boldFont;
    }
    
    private class YearInfo {
        public final List<Double> Distances;
        private double travelExpenses;
        
        public YearInfo() {
            this.Distances = new ArrayList<Double>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        }
        
        public double getTravelExpenses() {
            return this.travelExpenses;
        }
        
        public void addTravelExpense(double value) {
            this.travelExpenses += value;
        }
    }
}
