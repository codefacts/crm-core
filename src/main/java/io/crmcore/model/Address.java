package io.crmcore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

/**
 * Created by someone on 07-Jun-2015.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "addresses")
public class Address implements Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "varchar(500) null default null")
    private String house;
    @Column(columnDefinition = "varchar(500) null default null")
    private String street;
    @Column(columnDefinition = "varchar(500) null default null")
    private int postCode;
    @Column(columnDefinition = "varchar(500) null default null")
    private String postOffice;
    @Column(columnDefinition = "varchar(500) null default null")
    private String policeStation;
    @Column(columnDefinition = "varchar(500) null default null")
    private String district;
    @Column(columnDefinition = "varchar(500) null default null")
    private String division;
    @Column(columnDefinition = "varchar(500) null default null")
    private String country;
    @Column(columnDefinition = "varchar(1000) null default null")
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;

        Address address = (Address) o;

        return !(id != null ? !id.equals(address.id) : address.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", street='" + street + '\'' +
                ", postCode=" + postCode +
                ", district='" + district + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getPostCode() {
        return postCode;
    }

    public void setPostCode(int postCode) {
        this.postCode = postCode;
    }

    public String getPostOffice() {
        return postOffice;
    }

    public void setPostOffice(String postOffice) {
        this.postOffice = postOffice;
    }

    public String getPoliceStation() {
        return policeStation;
    }

    public void setPoliceStation(String policeStation) {
        this.policeStation = policeStation;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

