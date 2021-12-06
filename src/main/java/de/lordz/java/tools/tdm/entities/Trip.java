package de.lordz.java.tools.tdm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class Trip implements IEntityId {

    @Id
    @GeneratedValue(generator = "sqlite_trip")
    @TableGenerator(name = "sqlite_trip", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "tbTrip", initialValue = 1, allocationSize = 1)
    @Column(name = "coId")
    private int id;

    @Column(name = "coCustomerId")
    private int customerId;
    
    @Column(name = "coTripTypeId")
    private int tripTypeId;

    @Column(name = "coTimeOfTrip")
    private String timeOfTrip; 
    
    @Column(name = "coDescription", nullable = false)
    private String description;
    
    @Column(name = "coDeleted", nullable = false)
    private int deleted;

    @Transient
    private LocalDateTime localDateTime;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getCustomerId() {
        return this.customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public int getTripTypeId() {
        return this.tripTypeId;
    }
    
    public void setTripTypeId(int tripTypeId) {
        this.tripTypeId = tripTypeId;
    }
    
    public String getTimeOfTrip() {
        return this.timeOfTrip;
    }
    
    public void setTimeOfTrip(String timeOfTrip) {
        this.timeOfTrip = timeOfTrip;
        setLocalDateAndTime();
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setDeleted() {
        this.deleted = 1;
    }
    
    public LocalDateTime getLocalDateTime() {
        if (this.localDateTime == null) {
            setLocalDateAndTime();
        }
        
        return this.localDateTime;
    }
    
    public LocalDate getLocalDate() {
        var dateTimeInstance = getLocalDateTime();
        if (dateTimeInstance != null) {
            return dateTimeInstance.toLocalDate();
        }
        
        return null;
    }
    
    public LocalTime getLocalTime() {
        var dateTimeInstance = getLocalDateTime();
        if (dateTimeInstance != null) {
            return dateTimeInstance.toLocalTime();
        }
        
        return null;
    }
    
    private void setLocalDateAndTime() {
        if (!Strings.isNullOrEmpty(this.timeOfTrip)) {
            var date = DateTimeHelper.getDateFromIsoDateTime(this.timeOfTrip);
            if (date != null) {
                var utcInstant = date.toInstant().atZone(ZoneId.of("UTC"));
                this.localDateTime = utcInstant.toLocalDateTime();
            }
        }
    }
}

