package io.crmcore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by someone on 07-Jun-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {
    public static final String house = "house";
    public static final String street = "street";
    public static final String postCode = "postCode";
    public static final String postOffice = "postOffice";
    public static final String policeStation = "policeStation";

    public static final String district = "district";
    public static final String division = "division";
    public static final String country = "country";
    public static final String description = "description";
    Address() {}
}

