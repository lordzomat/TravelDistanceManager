package de.lordz.java.tools.tdm.entities;

import javax.persistence.*;

@Entity
@Table(name = "tbCustomers")
public class Customer implements IEntityId {

    @Id
    @GeneratedValue(generator = "sqlite_customers")
    @TableGenerator(name = "sqlite_customers", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "tbCustomers", initialValue = 1, allocationSize = 1)
    @Column(name = "coId")
    private int id;

    @Column(name = "coName", nullable = false)
    private String name;

    @Column(name = "coStreet", nullable = true)
    private String street;

    @Column(name = "coPostcode", nullable = true)
    private String postcode;

    @Column(name = "coCity", nullable = true)
    private String city;

    @Column(name = "coDescription", nullable = true)
    private String description;

    @Column(name = "coDistance", nullable = false)
    private double distance;

    @Column(name = "coDeleted", nullable = false)
    private int deleted;

    public int getId() {
        return this.id;
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

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostcode() {
        return this.postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setDeleted() {
        this.deleted = 1;
    }
        
    public String toString() {
        return this.name;
    }
}
