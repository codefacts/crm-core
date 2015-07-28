package io.crmcore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "consumers")
public class Consumer implements ConcreteUser<Long>, Model {
    //User basics
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

    //Concrete User
    @ManyToMany(fetch = FetchType.EAGER)
    protected Set<Role> roles;

    protected boolean active = true;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date joinDate;
    @Temporal(TemporalType.TIMESTAMP)
    protected Date resignDate;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;
    @Temporal(TemporalType.TIMESTAMP)
    protected Date modifyDate;
    @Column(length = 20, nullable = false)
    protected String createdBy;
    @Column(length = 20, nullable = false)
    protected String modifiedBy;

    //Consuer specifics
    @Column(columnDefinition = "varchar(250) null default null")
    private String fatherName;
    @Column(columnDefinition = "varchar(250) null default null")
    private String occupation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Consumer)) return false;

        Consumer consumer = (Consumer) o;

        return !(id != null ? !id.equals(consumer.id) : consumer.id != null);

    }

    @Override
    public String toString() {
        return "Consumer{" +
                "id=" + id +
                '}';
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    @Override
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String getMail() {
        return mail;
    }

    @Override
    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public Date getResignDate() {
        return resignDate;
    }

    public void setResignDate(Date resignDate) {
        this.resignDate = resignDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
