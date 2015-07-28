package io.crmcore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by someone on 15-Jul-2015.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "employees")
@Inheritance(strategy = InheritanceType.JOINED)
public class Employee implements UserInterface<Long>, Model {
    public static class Props {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Column(columnDefinition = "varchar(250) null default null")
    protected String firstName;
    @Column(columnDefinition = "varchar(250) null default null")
    protected String lastName;
    @Temporal(TemporalType.DATE)
    protected Date dateOfBirth;
    @Column(columnDefinition = "varchar(250) null default null")
    protected String mail;
    @Column(columnDefinition = "varchar(15) null default null")
    protected String phone;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(nullable = true)
    protected Address address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
