package io.crm.core.model;

public enum UserType {
    client("clients"), employee("employees"), consumer("consumers");

    public final String collection;

    UserType(final String collection) {
        this.collection = collection;
    }
}
