package io.crmcore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Br extends Employee {
    public static final String distributionHouse = "distributionHouse";
    public static final String brand = "brand";
    public static final String town = "town";
    Br() {}
}
