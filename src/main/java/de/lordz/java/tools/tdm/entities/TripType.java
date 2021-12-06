package de.lordz.java.tools.tdm.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "tbTripType")
public class TripType implements IEntityId {

    @Id
    @GeneratedValue(generator = "sqlite_triptype")
    @TableGenerator(name = "sqlite_triptype", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "tbTripType", initialValue = 1, allocationSize = 1)
    @Column(name = "coId")
    private int id;
    
    @Column(name = "coName", nullable = false)
    private String name;
    
    @Column(name = "coDeleted", nullable = false)
    private int deleted;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
        
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDeleted() {
        this.deleted = 1;
    }
    
    public String toString() {
        return this.name;
    }
}

