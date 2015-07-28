package io.crmcore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by sohan on 7/15/2015.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "locations")
public class Town implements Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true, columnDefinition = "varchar(500) not null")
    private String name;
    @ManyToOne
    private DistributionHouse distributionHouse;

    @Column(columnDefinition = "tinyint default 1")
    private boolean active = true;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifyDate;
    @Column(length = 20, nullable = false)
    protected String createdBy;
    @Column(length = 20, nullable = false)
    protected String modifiedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DistributionHouse getDistributionHouse() {
        return distributionHouse;
    }

    public void setDistributionHouse(DistributionHouse distributionHouse) {
        this.distributionHouse = distributionHouse;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
