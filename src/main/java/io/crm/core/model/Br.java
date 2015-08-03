package io.crm.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Br {
    public static final String distributionHouse = "distributionHouse";
    public static final String brand = "brand";
    public static final String mobile = "mobile";
    public static final String supervisor = "supervisor";
    public static final String location = "location";
    public static final String category = "category";
    public static final String joinDate = "joinDate";
    public static final String resignDate = "resignDate";
    public static final String remarks = "remarks";

    Br() {
    }
}
