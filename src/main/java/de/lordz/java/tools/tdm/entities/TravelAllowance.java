package de.lordz.java.tools.tdm.entities;

import java.time.LocalDate;
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
@Table(name = "tbTravelAllowance")
public class TravelAllowance implements IEntityId {
    @Id
    @GeneratedValue(generator = "sqlite_travelallowance")
    @TableGenerator(name = "sqlite_travelallowance", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "tbTravelAllowance", initialValue = 1, allocationSize = 1)
    @Column(name = "coId")
    private int id;
    
    @Column(name = "coRate", nullable = false)
    private double rate;
    
    @Column(name = "coValidFrom")
    private String validFromDateString;
    
    @Column(name = "coInvalidFrom")
    private String invalidFromDateString;
    
    @Column(name = "coDeleted", nullable = false)
    private int deleted;
    
    @Transient
    private LocalDate validFromDate;
    
    @Transient
    private LocalDate invalidFromDate;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
        
    public double getRate() {
        return this.rate;
    }
    
    public void setRate(double rate) {
        this.rate = rate;
    }
    
    public void setDeleted() {
        this.deleted = 1;
    }
    
    public String getValidFromDateString() {
        return this.validFromDateString;
    }
    
    public void setValidFromDate(String date) {
        this.validFromDateString = date;
        this.validFromDate = getLocalDate(date);
    }
      
    public LocalDate getValidFromDate() {
        if (this.validFromDate == null) {
            this.validFromDate = getLocalDate(this.validFromDateString);
        }
        
        return this.validFromDate;
    }
    
    public String getInvalidFromDateString() {
        return this.invalidFromDateString;
    }
    
    public void setInvalidFromDate(String date) {
        this.invalidFromDateString = date;
        this.invalidFromDate = getLocalDate(date);
    }
      
    public LocalDate getInvalidFromDate() {
        if (this.invalidFromDate == null) {
            this.invalidFromDate = getLocalDate(this.invalidFromDateString);
        }
        
        return this.invalidFromDate;
    }
        
    private LocalDate getLocalDate(String dateString) {
        LocalDate result = null;
        if (!Strings.isNullOrEmpty(dateString)) {
            var date = DateTimeHelper.getDateFromIsoDateTime(dateString);
            if (date != null) {
                var utcInstant = date.toInstant().atZone(ZoneId.of("UTC"));
                result = utcInstant.toLocalDate();
            }
        }
        
        return result;
    }
}
