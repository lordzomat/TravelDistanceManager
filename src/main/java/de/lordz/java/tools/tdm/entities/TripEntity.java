package de.lordz.java.tools.tdm.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import com.google.common.base.Strings;

import de.lordz.java.tools.tdm.common.DateTimeHelper;

@Entity
@Table(name = "tbTrip")
public class TripEntity {

    @Id
    @GeneratedValue(generator = "sqlite_trip")
    @TableGenerator(name = "sqlite_trip", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "tbTrip", initialValue = 1, allocationSize = 1)
    @Column(name = "coId")
    private int id;

    @Column(name = "coCustomerId")
    private int customerId;

    @Column(name = "coTimeOfTrip")
    private String timeOfTrip; 
    
    @Column(name = "coDescription", nullable = false)
    private String description;
    
    @Column(name = "coDeleted", nullable = false)
    private int deleted;
    
    @Transient
    private LocalDate localDate;
    
    @Transient
    private LocalTime localTime;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public String getTimeOfTrip() {
        return timeOfTrip;
    }
    
    public void setTimeOfTrip(String timeOfTrip) {
        this.timeOfTrip = timeOfTrip;
        setLocalDateAndTime();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setDeleted() {
        this.deleted = 1;
    }
    
    public LocalDate getLocalDate() {
        if (this.localDate == null) {
            setLocalDateAndTime();
        }
        
        return this.localDate;
    }
    
    public LocalTime getLocalTime() {
        if (this.localTime == null) {
            setLocalDateAndTime();
        }
        
        return this.localTime;
    }
    
    private void setLocalDateAndTime() {
        if (!Strings.isNullOrEmpty(this.timeOfTrip)) {
            var date = DateTimeHelper.getDateFromIsoDateTime(this.timeOfTrip);
            if (date != null) {
                var utcInstant = date.toInstant().atZone(ZoneId.of("UTC"));
                this.localDate = utcInstant.toLocalDate();
                this.localTime = utcInstant.toLocalTime();
            }
        }
    }
}
