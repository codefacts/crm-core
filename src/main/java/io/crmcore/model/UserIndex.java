package io.crmcore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

/**
 * Created by sohan on 7/15/2015.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "indices", uniqueConstraints = @UniqueConstraint(columnNames = {"userType", "actualId"}))
public class UserIndex implements Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //UserInterface specific basics
    @Column(length = 20, unique = true, nullable = false, updatable = false)
    private String userId;
    @Column(nullable = false, updatable = false)
    private UserType userType;
    @Column(nullable = false, updatable = false)
    private Long actualId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Long getActualId() {
        return actualId;
    }

    public void setActualId(Long actualId) {
        this.actualId = actualId;
    }
}
