package io.crmcore.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by sohan on 7/29/2015.
 */
public class User {
    public static class Props {
        public static final String firstName = "firstName";
        public static final String lastName = "lastName";
        public static final String dateOfBirth = "dateOfBirth";
        public static final String mail = "mail";
        public static final String phone = "phone";
        public static final String address = "address";
        public static final String roles = "roles";

        public static final String joinDate = "joinDate";
        public static final String resignDate = "resignDate";

        public static final String fatherName = "fatherName";
        public static final String occupation = "occupation";

        public static final String username = "username";
        public static final String password = "password";

        public static final String active = "active";
    }

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
}
