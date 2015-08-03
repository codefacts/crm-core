package io.crm.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsumerContact implements Serializable, Model {
    public static final String campaign = "campaign";
    public static final String region = "region";
    public static final String area = "area";
    public static final String distributionHouse = "distributionHouse";
    public static final String br = "br";
    public static final String consumer = "consumer";
    public static final String brand = "brand";
    public static final String name = "name";
    public static final String fatherName = "fatherName";
    public static final String mobile = "mobile";
    public static final String occupation = "occupation";

    public static final String age = "age";
    public static final String date = "date";

    public static final String description = "description";

    public static final String ptr = "ptr";
    public static final String swp = "swp";

    public static final String latitude = "latitude";
    public static final String longitude = "longitude";
    public static final String accuracy = "accuracy";
    public static final String location = "location";
    public static String receive_date = "receive_date";

    ConsumerContact() {
    }
}
