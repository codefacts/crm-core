package io.crmcore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by someone on 14-Jul-2015.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "head_office")
public class HeadOffice extends Employee implements ConcreteUserInterface<Long> {

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

    @Override
    public String toString() {
        return "HeadOffice{id=" + id + "}";
    }
}
