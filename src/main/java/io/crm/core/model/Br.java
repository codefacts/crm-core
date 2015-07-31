package io.crm.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Br extends Employee {
    public static final String distributionHouse = "distributionHouse";
    public static final String brand = "brand";
    public static final String town = "town";
    Br() {}
}
