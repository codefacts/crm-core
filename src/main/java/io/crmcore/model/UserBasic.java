package io.crmcore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.UUID;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "user_basics")
public class UserBasic implements Model {
    public static class Props {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(columnDefinition = "varchar(250) not null unique")
    protected String username;
    @Column(columnDefinition = "varchar(250) null default null")
    protected String password;
    @OneToOne
    private UserIndex userIndex;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserIndex getUserIndex() {
        return userIndex;
    }

    public void setUserIndex(UserIndex userIndex) {
        this.userIndex = userIndex;
    }
}
