package io.crm.core.model;


public class User {
    public static final String name = "name";
    public static final String dateOfBirth = "dateOfBirth";
    public static final String mail = "mail";
    public static final String mobile = "mobile";
    public static final String address = "address";
    public static final String roles = "roles";

    public static final String joinDate = "joinDate";
    public static final String resignDate = "resignDate";

    public static final String fatherName = "fatherName";
    public static final String occupation = "occupation";
    public static final String designation = "designation";

    public static final String username = "username";
    public static final String password = "password";

    public static final String active = "active";

    public static final String userId = "userId";
    public static final String userType = "userType";

    User() {
    }

    public static class Type {
        public static final String prefix = "prefix";
        public static final String label = "label";
    }
}
